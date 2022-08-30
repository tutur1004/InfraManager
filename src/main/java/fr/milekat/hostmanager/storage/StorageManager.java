package fr.milekat.hostmanager.storage;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.storage.adapter.mysql.MySQLAdapter;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.storage.exeptions.StorageLoaderException;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;

public class StorageManager {
    private final StorageExecutor executor;

    public StorageManager(@NotNull Configuration config) throws StorageLoaderException {
        if (config.getString("storage.type").equalsIgnoreCase("mysql") ||
                config.getString("storage.type").equalsIgnoreCase("mariadb")) {
            executor = new MySQLAdapter(config);
        } else {
            throw new StorageLoaderException("Unsupported storage type");
        }
        try {
            if (executor.checkStorages()) {
                if (Main.DEBUG) {
                    Main.getHostLogger().info("Storage loaded");
                }
            } else {
                throw new StorageLoaderException("Storages are not loaded properly");
            }
        } catch (StorageExecuteException exception) {
            throw new StorageLoaderException("Can't load storage properly");
        }

    }

    public StorageExecutor getStorageExecutor() {
        return this.executor;
    }
}
