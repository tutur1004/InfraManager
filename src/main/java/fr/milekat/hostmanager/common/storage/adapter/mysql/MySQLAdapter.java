package fr.milekat.hostmanager.common.storage.adapter.mysql;

import fr.milekat.hostmanager.api.classes.*;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.storage.StorageExecutor;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.common.storage.exeptions.StorageLoaderException;
import fr.milekat.hostmanager.common.utils.Configs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class MySQLAdapter implements StorageExecutor {
    private final String SCHEMA_FILE = "host_schema.sql";
    private final long CACHE_DURATION = TimeUnit.MILLISECONDS.convert(30L, TimeUnit.MINUTES);
    private final MySQLPool DB;
    private final String PREFIX = Main.getConfig().getString("storage.mysql.prefix");
    private final List<String> TABLES = Arrays.asList(PREFIX + "games", PREFIX + "instances", PREFIX + "logs", PREFIX + "users");
    private Date CACHED_GAMES_REFRESH = null;
    private List<Game> CACHED_GAMES = new ArrayList<>();

    /*
        SQL Queries
     */
    private final String CHECK_TABLE = "SELECT TABLE_NAME FROM information_schema.TABLES " +
            "WHERE TABLE_NAME = ?;";

    private final String GET_TICKETS = "SELECT tickets FROM {prefix}users " +
            "WHERE uuid = ?;";
    private final String GET_GAMES = "SELECT * FROM {prefix}games;";
    private final String GET_GAME = "SELECT * FROM {prefix}games WHERE game_name = ?;";
    private final String GET_USERS = "SELECT * FROM {prefix}users;";
    private final String GET_USER = "SELECT * FROM {prefix}users WHERE last_name = ?;";
    private final String GET_USER_UUID = "SELECT * FROM {prefix}users WHERE uuid = ?;";
    private final String GET_ACTIVE_INSTANCES = "SELECT * FROM {prefix}instances i " +
            "INNER JOIN {prefix}games g ON i.game=g.game_id " +
            "INNER JOIN {prefix}users u ON i.user=u.user_id " +
            "WHERE i.state <>4;";
    private final String GET_INSTANCE = "SELECT * FROM {prefix}instances i " +
            "INNER JOIN {prefix}games g ON i.game=g.game_id " +
            "INNER JOIN {prefix}users u ON i.user=u.user_id " +
            "WHERE i.state <>4 AND i.instance_name = ?;";
    private final String GET_N_LOGS = "SELECT * FROM {prefix}logs ORDER BY log_id DESC LIMIT ?;";
    private final String GET_LOGS_WITHIN_DATE = "SELECT * FROM {prefix}logs l " +
            "INNER JOIN {prefix}instances i ON l.instance = i.instance_id " +
            "INNER JOIN {prefix}users u on l.user = u.user_id " +
            "INNER JOIN {prefix}games g on l.game = g.game_id " +
            "WHERE (l.log_date BETWEEN ? AND ?);";
    private final String FIND_AVAILABLE_PORTS = "SELECT port FROM {prefix}instances " +
            "WHERE state <>4;";
    private final String FETCH_GAME_CONFIGS = "SELECT props.property_name as var, props.value as val " +
            "FROM {prefix}properties props " +
            "INNER JOIN {prefix}profiles prof ON props.profile=prof.profile_id " +
            "LEFT JOIN {prefix}game_strategies str ON props.profile=str.profile " +
            "WHERE props.enable=1 AND (prof.profile_name='global' OR (str.game=? AND prof.enable=1)) " +
            "ORDER BY props.profile DESC;";

    private final String ADD_TICKETS = "INSERT INTO {prefix}users (uuid, last_name, tickets) " +
            "VALUES (?,?,?) ON DUPLICATE KEY UPDATE last_name = ?, tickets = tickets + ?;";
    private final String REMOVE_TICKETS = "INSERT INTO {prefix}users (uuid, last_name, tickets) " +
            "VALUES (?,?,?) ON DUPLICATE KEY UPDATE last_name = ?, tickets = tickets - ?;";
    private final String CREATE_GAME = "INSERT INTO {prefix}games " +
            "(name, enable, image, requirements) " +
            "VALUES (?,?,?,?);";
    private final String CREATE_INSTANCE = "INSERT INTO {prefix}instances " +
            "(instance_name, instance_description, instance_server_id, port, state, game, user, creation) " +
            "VALUES (?,?,?,?,?,?,?,?);";

    private final String UPDATE_GAME = "UPDATE {prefix}games " +
            "SET name=?, enable=?, image=?, requirements=?";
    private final String UPDATE_INSTANCE = "UPDATE {prefix}instances " +
            "SET instance_server_id=?, state=?, game=?, user=? " +
            "WHERE instance_name = ?";

    /**
     * Format query by replacing {prefix} with {@link MySQLAdapter#PREFIX}
     */
    @Contract(pure = true)
    private @NotNull String formatQuery(@NotNull String query) {
        return query.replaceAll("\\{prefix}", PREFIX);
    }

    public MySQLAdapter(Configs config) throws StorageLoaderException {
        DB = new MySQLPool(config);
        try {
            if (!checkStorages()) {
                applySchema();
            }
        } catch (StorageExecuteException | IOException exception) {
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
    private void applySchema() throws IOException, StorageLoaderException {
        List<String> statements;
        //  Read schema file
        try (InputStream schemaFileIS = this.getClass().getResourceAsStream(SCHEMA_FILE)) {
            if (schemaFileIS == null) {
                throw new StorageLoaderException("Missing schema file");
            }
            statements = MySQLUtils.getQueries(schemaFileIS).stream()
                    .map(this::formatQuery)
                    .collect(Collectors.toList());
        }
        //  Apply Schema
        try (Connection connection = DB.getConnection();
                Statement s = connection.createStatement()) {
            connection.setAutoCommit(false);
            for (String query : statements) {
                s.addBatch(query);
            }
            s.executeBatch();
        } catch (Exception exception) {
            if (!exception.getMessage().contains("already exists") && Main.DEBUG) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Check if all tables are created
     * @return true if all tables are created
     */
    @Override
    public boolean checkStorages() throws StorageExecuteException {
        try (Connection connection = DB.getConnection()) {
            for (String table : TABLES) {
                try (PreparedStatement q = connection.prepareStatement(formatQuery(CHECK_TABLE))) {
                    q.setString(1, table);
                    q.execute();
                    if (!q.getResultSet().next()) {
                        if (Main.DEBUG) {
                            Main.getLogger().warn("Table: " + table + " is not loaded properly");
                        }
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, "Missing schema file");
        }
    }

    /*
        Tickets
     */

    /**
     * Query tickets for this player
     * @param uuid player uuid
     * @return amount of reaming ticket
     */
    @Override
    public Integer getTicket(@NotNull UUID uuid) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_TICKETS))) {
            q.setString(1, uuid.toString());
            q.execute();
            if (q.getResultSet().next()) {
                return q.getResultSet().getInt("tickets");
            }
            return 0;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
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
        updateUser(uuid, username, amount, ADD_TICKETS);
    }

    /**
     * Remove tickets to this player
     * @param uuid player uuid
     * @param username player minecraft username
     * @param amount amount of tickets to remove to this player
     */
    @Override
    public void removePlayerTickets(UUID uuid, String username, Integer amount) throws StorageExecuteException {
        updateUser(uuid, username, amount, REMOVE_TICKETS);
    }

    /*
        Games
     */

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
            CACHED_GAMES = games;
            CACHED_GAMES_REFRESH = new Date();
            return games;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Get last queried list of games (If list is too old, or not exist, it will re-queried the list)
     * @return "recent" list of games
     */
    @Override
    public List<Game> getGamesCached() throws StorageExecuteException {
        if (CACHED_GAMES_REFRESH.getTime() + CACHE_DURATION < new Date().getTime()) {
            CACHED_GAMES = getGames();
        }
        return CACHED_GAMES;
    }

    /**
     * Query a games by name
     * @return game or null if not exist
     */
    @Override
    public Game getGame(String gameName) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_GAME))) {
            q.setString(1, gameName);
            q.execute();
            if (q.getResultSet().next()) {
                return resultSetToGame(q.getResultSet());
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
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

    private void gameQuery(@NotNull Game game, String query) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(query))) {
            q.setString(1, game.getName());
            q.setBoolean(2, game.isEnable());
            q.setString(3, game.getImage());
            q.setInt(4, game.getRequirements());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /*
        Instance
     */

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
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Query an instance
     * @param name of instance
     * @return instance (If exist)
     */
    @Override
    public @Nullable Instance getInstance(String name) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_INSTANCE))) {
            q.setString(1, name);
            q.execute();
            if (q.getResultSet().next()) {
                return resultSetToInstance(q.getResultSet());
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public void createInstance(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(CREATE_INSTANCE))) {
            q.setString(1, instance.getName());
            q.setString(2, instance.getDescription());
            q.setString(3, instance.getServerId());
            q.setInt(4, instance.getPort());
            q.setInt(5, instance.getState().getStateId());
            q.setInt(6, instance.getGame().getId());
            q.setInt(7, instance.getHost().getId());
            q.setTimestamp(8, new Timestamp(instance.getCreation().getTime()));
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public void updateInstance(@NotNull Instance instance) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(UPDATE_INSTANCE))) {
            q.setString(1, instance.getServerId());
            q.setInt(2, instance.getState().getStateId());
            q.setInt(3, instance.getGame().getId());
            q.setInt(4, instance.getHost().getId());
            q.setString(5, instance.getName());
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    @Override
    public @Nullable Integer findAvailablePort(List<Integer> ports) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(FIND_AVAILABLE_PORTS))) {
            q.execute();
            List<Integer> usedPorts = new ArrayList<>();
            while (q.getResultSet().next()) {
                usedPorts.add(q.getResultSet().getInt("port"));
            }
            for (Integer port : ports) {
                if (!usedPorts.contains(port)) {
                    return port;
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
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
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Query a user by his name if present, otherwise return null
     * @param name of player
     * @return User or null
     */
    @Override
    public @Nullable User getUser(String name) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_USER))) {
            q.setString(1, name);
            q.execute();
            if (q.getResultSet().next()) {
                return resultSetToUser(q.getResultSet());
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Query a user if present, otherwise return null
     * @param uuid of player
     * @return User or null
     */
    @Override
    public @Nullable User getUser(@NotNull UUID uuid) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(GET_USER_UUID))) {
            q.setString(1, uuid.toString());
            q.execute();
            if (q.getResultSet().next()) {
                return resultSetToUser(q.getResultSet());
            } else return null;
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    private void updateUser(@NotNull UUID uuid, String username, Integer amount, String query) throws StorageExecuteException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(query))) {
            q.setString(1, uuid.toString());
            q.setString(2, username);
            q.setInt(3, amount);
            q.setString(4, username);
            q.setInt(5, amount);
            q.execute();
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
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
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /**
     * Retrieve all logs between 2 days ! WARNING THIS CAN BE A HUGE STORAGE QUERY
     * @param from first date from the period
     * @param to end of the period
     */
    @Override
    public List<Log> getLogs(@NotNull Date from, @NotNull Date to) throws StorageExecuteException {
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
        } catch (SQLException exception) {
            throw new StorageExecuteException(exception, exception.getSQLState());
        }
    }

    /*
        Class shortcuts
     */
    /**
     * Shortcut to convert MySQL instance row into instance class
     */
    @Contract("_ -> new")
    private @NotNull Instance resultSetToInstance(@NotNull ResultSet r) throws SQLException {
        return new Instance(r.getInt("instance_id"),
                r.getString("instance_name"),
                r.getString("instance_server_id"),
                r.getString("instance_description"),
                r.getString("instance_message"),
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
    @Contract("_ -> new")
    private @NotNull Game resultSetToGame(@NotNull ResultSet r) throws SQLException {
        return new Game(r.getInt("game_id"),
                r.getString("game_name"),
                new Date(r.getTimestamp("create_date").getTime()),
                r.getBoolean("enable"),
                r.getString("game_version"),
                r.getString("server_version"),
                r.getString("image"),
                r.getInt("requirements"),
                fetchConfigs(r.getInt("game_id")));
    }

    /**
     * Shortcut to convert MySQL log row into log class
     */
    @Contract("_ -> new")
    private @NotNull Log resultSetToLog(@NotNull ResultSet r) throws SQLException {
        return new Log(new Date(r.getTimestamp("log_date").getTime()),
                resultSetToInstance(r),
                LogAction.fromInteger(r.getInt("action")),
                resultSetToUser(r),
                resultSetToGame(r));
    }

    /**
     * Shortcut to convert MySQL user row into user class
     */
    @Contract("_ -> new")
    private @NotNull User resultSetToUser(@NotNull ResultSet r) throws SQLException {
        return new User(r.getInt("user_id"),
                UUID.fromString(r.getString("uuid")),
                r.getString("last_name"),
                r.getInt("tickets"));
    }

    /**
     * Shortcut to fetch all game configs
     */
    private @NotNull Map<String, String> fetchConfigs(int gameId) throws SQLException {
        try (Connection connection = DB.getConnection();
             PreparedStatement q = connection.prepareStatement(formatQuery(FETCH_GAME_CONFIGS))) {
            q.setInt(1, gameId);
            q.execute();
            Map<String, String> configs = new HashMap<>();
            while (q.getResultSet().next()) {
                configs.put(q.getResultSet().getString("var"), q.getResultSet().getString("val"));
            }
            return configs;
        }
    }
}
