package fr.milekat.hostmanager.storage;

import fr.milekat.hostmanager.storage.exeptions.StorageLoaderException;
import fr.milekat.hostmanager.storage.mysql.MySQLAdapter;
import net.md_5.bungee.config.Configuration;

public class StorageManager {
    private final StorageExecutor executor;

    public StorageManager(Configuration config) throws StorageLoaderException {
        if (config.getString("database.type").equalsIgnoreCase("mysql") ||
                config.getString("database.type").equalsIgnoreCase("mariadb")) {
            executor = new MySQLAdapter(config);
        } else {
            throw new StorageLoaderException("Unsupported database type");
        }
        if (!executor.checkStorages()) {
            throw new StorageLoaderException("Storages are not loaded properly");
        }
    }

    public StorageExecutor getExecutor() {
        return this.executor;
    }
}
