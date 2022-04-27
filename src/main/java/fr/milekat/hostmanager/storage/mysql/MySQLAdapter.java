package fr.milekat.hostmanager.storage.mysql;

import fr.milekat.hostmanager.HostManager;
import fr.milekat.hostmanager.storage.StorageExecutor;
import fr.milekat.hostmanager.storage.StorageLoaderException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("FieldCanBeLocal")
public class MySQLAdapter implements StorageExecutor {
    private final String SCHEMA_FILE = "host_schema.sql";
    private final FileConfiguration CONFIG;
    private final MySQLDriver DB;
    private final List<String> TABLES = Arrays.asList("host_games", "host_instances", "host_logs", "host_users");

    /*
        SQL Queries
     */
    private final String CHECK_TABLE = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_NAME = ?;";
    private final String GET_TICKETS = "SELECT tickets FROM host_users WHERE uuid = '?';";
    private final String ADD_TICKETS = "INSERT INTO host_users (uuid, last_name, tickets) VALUES (?,?,?) ON DUPLICATE KEY UPDATE tickets = tickets + ?;";


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
            if (HostManager.DEBUG) {
                throwable.printStackTrace();
            }
            throw new StorageLoaderException("Unsupported database type");
        }
    }

    /**
     * Disconnect from MySQL server
     */
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
                if (!throwable.getMessage().contains("already exists") && HostManager.DEBUG) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if all tables are created
     * @return true if all tables are created
     */
    public boolean checkStorages() {
        try {
            Connection connection = DB.getConnection();
            for (String table : TABLES) {
                PreparedStatement q = connection.prepareStatement(CHECK_TABLE);
                q.setString(1, table);
                q.execute();
                if (!q.getResultSet().next()) {
                    if (HostManager.DEBUG) {
                        HostManager.getHostLogger().warning("Table: " + table + " is not loaded properly");
                    }
                    q.close();
                    return false;
                }
                q.close();
            }
            return true;
        } catch (SQLException throwable) {
            if (HostManager.DEBUG) {
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
    public Integer getTicket(UUID uuid) throws SQLException {
        Connection connection = DB.getConnection();
        PreparedStatement q = connection.prepareStatement(GET_TICKETS);
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
    }

    /**
     * Add tickets to this player
     * @param uuid player uuid
     * @param username player minecraft username
     * @param amount amount of tickets to add to this player
     */
    public void addPlayerTickets(UUID uuid, String username, Integer amount) throws SQLException {
        Connection connection = DB.getConnection();
        PreparedStatement q = connection.prepareStatement(ADD_TICKETS);
        q.setString(1, uuid.toString());
        q.setString(2, username);
        q.setInt(3, amount);
        q.setInt(4, amount);
        q.execute();
    }
}
