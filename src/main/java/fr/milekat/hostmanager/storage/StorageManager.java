package fr.milekat.hostmanager.storage;

import fr.milekat.hostmanager.storage.mysql.MySQLAdapter;
import org.bukkit.configuration.file.FileConfiguration;

public class StorageManager {
    private final StorageExecutor executor;

    public StorageManager(FileConfiguration config) throws StorageLoaderException {
        if (config.getString("database.type").equalsIgnoreCase("mysql")) {
            executor = new MySQLAdapter(config);
        } else {
            throw new StorageLoaderException("Unsupported database type");
        }
    }

    public StorageExecutor getExecutor() {
        return this.executor;
    }
}
