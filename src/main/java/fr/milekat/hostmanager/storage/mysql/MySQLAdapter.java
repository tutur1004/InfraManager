package fr.milekat.hostmanager.storage.mysql;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.hosts.classes.Game;
import fr.milekat.hostmanager.storage.StorageExecutor;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.storage.exeptions.StorageLoaderException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("FieldCanBeLocal")
public class MySQLAdapter implements StorageExecutor {
    private final String SCHEMA_FILE = "host_schema.sql";
    private final FileConfiguration CONFIG;
    private final MySQLDriver DB;
    private final String PREFIX = Main.getFileConfig().getString("database.mysql.prefix");
    private final List<String> TABLES = Arrays.asList(PREFIX + "games", PREFIX + "instances", PREFIX + "logs", PREFIX + "users");

    /*
        SQL Queries
     */
    private final String CHECK_TABLE = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_NAME = ?;";

    private final String GET_TICKETS = "SELECT tickets FROM {prefix}users WHERE uuid = '?';";
    private final String GET_GAMES = "SELECT * FROM {prefix}users WHERE uuid = '?';";

    private final String ADD_TICKETS = "INSERT INTO {prefix}users (uuid, last_name, tickets) VALUES (?,?,?) ON DUPLICATE KEY UPDATE tickets = tickets + ?;";
    private final String REMOVE_TICKETS = "INSERT INTO {prefix}users (uuid, last_name, tickets) VALUES (?,?,?) ON DUPLICATE KEY UPDATE tickets = tickets - ?;";
    private final String CREATE_GAME = "INSERT INTO {prefix}games (name, enable, image, requirements) VALUES (?,?,?,?);";

    private final String UPDATE_GAME = "UPDATE {prefix}games SET name=?, enable=?, image=?, requirements=?";

    /**
     * Format query by replacing {prefix} with {@link MySQLAdapter#PREFIX}
     */
    private String formatQuery(String query) {
        return query.replaceAll("\\{prefix}", PREFIX);
    }

    public MySQLAdapter(FileConfiguration config) throws StorageLoaderException {
        this.CONFIG = config;
        try {
            DB = new MySQLDriver("jdbc:mysql://",
                    config.getString("database.mysql.hostname"),
                    config.getString("database.mysql.database"),
                    config.getString("database.mysql.username"),
                    config.getString("database.mysql.password"));
            DB.connection();
            applySchema();
        } catch (SQLException | IOException throwable) {
            if (Main.DEBUG) {
                throwable.printStackTrace();
            }
            throw new StorageLoaderException("Unsupported database type");
        }
    }

    /**
     * Disconnect from MySQL server
     */
    @Override
    public void disconnect() {
        DB.disconnect();
    }

    /**
     * Apply SQL Default schema with host_schema.sql dump
     */
    private void applySchema() throws SQLException, IOException, StorageLoaderException {
        //  Read schema file
        InputStream schemaFileIS = MySQLDriver.class.getResourceAsStream(SCHEMA_FILE);
        if (schemaFileIS==null) {
            throw new StorageLoaderException("Missing schema file");
        } else {
            InputStreamReader streamFile = new InputStreamReader(schemaFileIS);
            BufferedReader bufferedReader = new BufferedReader(streamFile);
            //  Apply Schema
            Connection connection = DB.getConnection();
            PreparedStatement q = connection.prepareStatement(bufferedReader.lines()
                    .filter(line -> !line.startsWith("--"))
                    .collect(Collectors.joining()
                    ).replaceAll("\\{prefix}", CONFIG.getString("database.mysql.prefix")));
            try {
                q.execute();
            } catch (Exception throwable) {
                if (!throwable.getMessage().contains("already exists") && Main.DEBUG) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if all tables are created
     * @return true if all tables are created
     */
    @Override
    public boolean checkStorages() {
        try {
            Connection connection = DB.getConnection();
            for (String table : TABLES) {
                PreparedStatement q = connection.prepareStatement(formatQuery(CHECK_TABLE));
                q.setString(1, table);
                q.execute();
                if (!q.getResultSet().next()) {
                    if (Main.DEBUG) {
                        Main.getHostLogger().warning("Table: " + table + " is not loaded properly");
                    }
                    q.close();
                    return false;
                }
                q.close();
            }
            return true;
        } catch (SQLException throwable) {
            if (Main.DEBUG) {
                throwable.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Query tickets for this player
     * @param uuid player uuid
     * @return amount of reaming ticket
     */
    @Override
    public Integer getTicket(UUID uuid) throws StorageExecuteException {
        try {
            Connection connection = DB.getConnection();
            PreparedStatement q = connection.prepareStatement(formatQuery(GET_TICKETS));
            q.setString(1, uuid.toString());
            q.execute();
            if (q.getResultSet().next()) {
                int tickets = q.getResultSet().getInt("tickets");
                q.close();
                return tickets;
            } else {
                q.close();
                return 0;
            }
        } catch (SQLException throwable) {
            throw new StorageExecuteException(throwable.getCause(), throwable.getSQLState());
        }
    }

    /**
     * Add tickets to this player
     * @param uuid player uuid
     * @param username player minecraft username
     * @param amount amount of tickets to add to this player
     */
    @Override
    public void addPlayerTickets(UUID uuid, String username, Integer amount) throws StorageExecuteException {
        updatePlayerTickets(uuid, username, amount, ADD_TICKETS);
    }

    /**
     * Remove tickets to this player
     * @param uuid player uuid
     * @param username player minecraft username
     * @param amount amount of tickets to remove to this player
     */
    @Override
    public void removePlayerTickets(UUID uuid, String username, Integer amount) throws StorageExecuteException {
        updatePlayerTickets(uuid, username, amount, REMOVE_TICKETS);
    }

    private void updatePlayerTickets(UUID uuid, String username, Integer amount, String query) throws StorageExecuteException {
        try {
            Connection connection = DB.getConnection();
            PreparedStatement q = connection.prepareStatement(formatQuery(query));
            q.setString(1, uuid.toString());
            q.setString(2, username);
            q.setInt(3, amount);
            q.setInt(4, amount);
            q.execute();
        } catch (SQLException throwable) {
            throw new StorageExecuteException(throwable.getCause(), throwable.getSQLState());
        }
    }

    /**
     * Query all games
     * @return list of games
     */
    @Override
    public List<Game> getGames() throws StorageExecuteException {
        try {
            Connection connection = DB.getConnection();
            PreparedStatement q = connection.prepareStatement(formatQuery(GET_GAMES));
            q.execute();
            List<Game> games = new ArrayList<>();
            while (q.getResultSet().next()) {
                games.add(new Game(q.getResultSet().getInt("id"),
                        q.getResultSet().getString("name"),
                        new Date(q.getResultSet().getTimestamp("create_date").getTime()),
                        q.getResultSet().getBoolean("enable"),
                        q.getResultSet().getString("image"),
                        q.getResultSet().getInt("requirements"))
                );
            }
            return games;
        } catch (SQLException throwable) {
            throw new StorageExecuteException(throwable.getCause(), throwable.getSQLState());
        }
    }

    /**
     * Create a new game
     */
    @Override
    public void createGame(Game game) throws StorageExecuteException {
        gameQuery(game, CREATE_GAME);
    }

    /**
     * Update an existing game
     */
    @Override
    public void updateGame(Game game) throws StorageExecuteException {
        gameQuery(game, UPDATE_GAME);
    }

    private void gameQuery(Game game, String query) throws StorageExecuteException {
        try {
            Connection connection = DB.getConnection();
            PreparedStatement q = connection.prepareStatement(formatQuery(query));
            q.setString(1, game.getName());
            q.setBoolean(2, game.isEnable());
            q.setString(3, game.getImage());
            q.setInt(4, game.getRequirements());
            q.execute();
        } catch (SQLException throwable) {
            throw new StorageExecuteException(throwable.getCause(), throwable.getSQLState());
        }
    }
}
