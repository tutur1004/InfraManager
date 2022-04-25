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
import java.util.UUID;
import java.util.stream.Collectors;

public class MySQLAdapter implements StorageExecutor {
    private final String SCHEMA_FILE = "host_schema.sql";
    private final FileConfiguration CONFIG;
    private final MySQLDriver DB;

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
                    ).replaceAll("\\{prefix\\}", CONFIG.getString("database.mysql.prefix")));
            try {
                q.execute();
            } catch (Exception throwable) {
                if (!throwable.getMessage().contains("already exists") && HostManager.DEBUG) {
                    throwable.printStackTrace();
                }
            }
            connection.close();
        }
    }

    /**
     * Query tickets for this uuid
     * @param uuid player uuid
     * @return amount of reaming ticket
     */
    public Integer getTicket(UUID uuid) {
        // TODO: 25/04/2022 MySQL - Query tickets for this uuid
        return 0;
    }
}
