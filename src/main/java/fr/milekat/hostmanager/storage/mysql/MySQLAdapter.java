package fr.milekat.hostmanager.storage.mysql;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.*;
import fr.milekat.hostmanager.storage.StorageExecutor;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.storage.exeptions.StorageLoaderException;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@SuppressWarnings("FieldCanBeLocal")
public class MySQLAdapter implements StorageExecutor {
    private final String SCHEMA_FILE = "host_schema.sql";
    private final Configuration CONFIG;
    private final MySQLPool DB;
    private final String PREFIX = Main.getFileConfig().getString("database.mysql.prefix");
    private final List<String> TABLES = Arrays.asList(PREFIX + "games", PREFIX + "instances", PREFIX + "logs", PREFIX + "users");

    /*
        SQL Queries
     */
    private final String CHECK_TABLE = "SELECT TABLE_NAME FROM information_schema.TABLES " +
            "WHERE TABLE_NAME = ?;";

    private final String GET_TICKETS = "SELECT tickets FROM {prefix}users " +
            "WHERE uuid = '?';";
    private final String GET_GAMES = "SELECT * FROM {prefix}games;";
    private final String GET_USERS = "SELECT * FROM {prefix}users;";
    private final String GET_ACTIVE_INSTANCES = "SELECT * FROM {prefix}instances i " +
            "INNER JOIN {prefix}games g ON i.game=g.game_id " +
            "WHERE i.state <>4;";
    private final String GET_N_LOGS = "SELECT * FROM {prefix}logs ORDER BY log_id DESC LIMIT ?;";
    private final String GET_LOGS_WITHIN_DATE = "SELECT * FROM {prefix}logs l " +
            "INNER JOIN {prefix}instances i ON l.instance = i.instance_id " +
            "INNER JOIN {prefix}users u on l.user = u.user_id " +
            "INNER JOIN {prefix}games g on l.game = g.game_id " +
            "WHERE (l.log_date BETWEEN ? AND ?);";

    private final String ADD_TICKETS = "INSERT INTO {prefix}users (uuid, last_name, tickets) " +
            "VALUES (?,?,?) ON DUPLICATE KEY UPDATE tickets = tickets + ?;";
    private final String REMOVE_TICKETS = "INSERT INTO {prefix}users (uuid, last_name, tickets) " +
            "VALUES (?,?,?) ON DUPLICATE KEY UPDATE tickets = tickets - ?;";
    private final String CREATE_GAME = "INSERT INTO {prefix}games (name, enable, image, requirements) " +
            "VALUES (?,?,?,?);";

    private final String UPDATE_GAME = "UPDATE {prefix}games SET name=?, enable=?, image=?, requirements=?";

    /**
     * Format query by replacing {prefix} with {@link MySQLAdapter#PREFIX}
     */
    private String formatQuery(String query) {
        return query.replaceAll("\\{prefix}", PREFIX);
    }

    public MySQLAdapter(Configuration config) throws StorageLoaderException {
        this.CONFIG = config;
        try {
            DB = new MySQLPool(config);
            applySchema();
        } catch (SQLException | IOException throwable) {
            if (Main.DEBUG) {
                throwable.printStackTrace();
            }
            throw new StorageLoaderException("Unsupported database type");
        }
    }

    /**
     * Disconnect from HikariCP pool
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
            try (Connection connection = DB.getConnection();
                 PreparedStatement q = connection.prepareStatement(bufferedReader.lines()
                         .filter(line -> !line.startsWith("--"))
                         .collect(Collectors.joining())
                         .replaceAll("\\{prefix}", CONFIG.getString("database.mysql.prefix")))) {
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
        try (Connection connection = DB.getConnection()) {
            for (String table : TABLES) {
                try (PreparedStatement q = connection.prepareStatement(formatQuery(CHECK_TABLE))) {
                    q.setString(1, table);
                    q.execute();
                    if (!q.getResultSet().next()) {
                        if (Main.DEBUG) {
                            Main.getHostLogger().warning("Table: " + table + " is not loaded properly");
                        }
                        return false;
                    }
                }
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
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_TICKETS))) {
            q.setString(1, uuid.toString());
            q.execute();
            if (q.getResultSet().next()) {
                return q.getResultSet().getInt("tickets");
            }
            return 0;
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
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(query))) {
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
     * Query active instances
     * @return list of instances
     */
    @Override
    public List<Instance> getActiveInstances() throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_ACTIVE_INSTANCES))) {
            q.execute();
            List<Instance> instances = new ArrayList<>();
            while (q.getResultSet().next()) {
                instances.add(resultSetToInstance(q.getResultSet()));
            }
            return instances;
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
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_GAMES))) {
            q.execute();
            List<Game> games = new ArrayList<>();
            while (q.getResultSet().next()) {
                games.add(resultSetToGame(q.getResultSet()));
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
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(query))) {
            q.setString(1, game.getName());
            q.setBoolean(2, game.isEnable());
            q.setString(3, game.getImage());
            q.setInt(4, game.getRequirements());
            q.execute();
        } catch (SQLException throwable) {
            throw new StorageExecuteException(throwable.getCause(), throwable.getSQLState());
        }
    }

    /*
        Users
     */
    /**
     * Query all users ! WARNING THIS CAN BE A HUGE STORAGE QUERY
     * @return all users
     */
    @Override
    public List<User> getUsers() throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_USERS))) {
            q.execute();
            List<User> games = new ArrayList<>();
            while (q.getResultSet().next()) {
                games.add(resultSetToUser(q.getResultSet()));
            }
            return games;
        } catch (SQLException throwable) {
            throw new StorageExecuteException(throwable.getCause(), throwable.getSQLState());
        }
    }

    /**
     * Query a user if present, otherwise return null
     * @param uuid of player
     * @return User or null
     */
    @Override
    public @Nullable User getUser(UUID uuid) throws StorageExecuteException {
        return null;
    }

    /*
        Logs
     */
    /**
     * Retrieve last n logs
     * @param count number of the latest logs to retrieve
     */
    @Override
    public List<Log> getLogs(int count) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_N_LOGS))) {
            q.setInt(1, count);
            q.execute();
            List<Log> logs = new ArrayList<>();
            while (q.getResultSet().next()) {
                logs.add(resultSetToLog(q.getResultSet()));
            }
            return logs;
        } catch (SQLException throwable) {
            throw new StorageExecuteException(throwable.getCause(), throwable.getSQLState());
        }
    }

    /**
     * Retrieve all logs between 2 days ! WARNING THIS CAN BE A HUGE STORAGE QUERY
     * @param from first date from the period
     * @param to end of the period
     */
    @Override
    public List<Log> getLogs(Date from, Date to) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_LOGS_WITHIN_DATE))) {
            q.setTimestamp(1, new Timestamp(from.getTime()));
            q.setTimestamp(2, new Timestamp(to.getTime()));
            q.execute();
            List<Log> logs = new ArrayList<>();
            while (q.getResultSet().next()) {
                logs.add(resultSetToLog(q.getResultSet()));
            }
            return logs;
        } catch (SQLException throwable) {
            throw new StorageExecuteException(throwable.getCause(), throwable.getSQLState());
        }
    }

    /*
        Class shortcuts
     */
    /**
     * Shortcut to convert MySQL instance row into instance class
     */
    private Instance resultSetToInstance(ResultSet r) throws SQLException {
        return new Instance(r.getString("name"),
                r.getString("name"),
                r.getInt("port"),
                InstanceState.fromInteger(r.getInt("state")),
                resultSetToGame(r),
                resultSetToUser(r),
                new Date(r.getTimestamp("creation").getTime()),
                new Date(r.getTimestamp("deletion").getTime())
        );
    }

    /**
     * Shortcut to convert MySQL game row into game class
     */
    private Game resultSetToGame(ResultSet r) throws SQLException {
        return new Game(r.getString("name"),
                new Date(r.getTimestamp("create_date").getTime()),
                r.getBoolean("enable"),
                r.getString("game_version"),
                r.getString("server_version"),
                r.getString("image"),
                r.getInt("requirements"));
    }

    /**
     * Shortcut to convert MySQL log row into log class
     */
    private Log resultSetToLog(ResultSet r) throws SQLException {
        return new Log(new Date(r.getTimestamp("log_date").getTime()),
                resultSetToInstance(r),
                LogAction.fromInteger(r.getInt("action")),
                resultSetToUser(r),
                resultSetToGame(r));
    }

    /**
     * Shortcut to convert MySQL user row into user class
     */
    private User resultSetToUser(ResultSet r) throws SQLException {
        return new User(UUID.fromString(r.getString("uuid")),
                r.getString("last_name"),
                r.getInt("tickets"));
    }
}
