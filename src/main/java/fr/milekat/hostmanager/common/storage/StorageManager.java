package fr.milekat.hostmanager.common.storage;

import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.storage.adapter.mysql.MySQLAdapter;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.common.storage.exeptions.StorageLoaderException;
import fr.milekat.hostmanager.common.utils.Configs;
import org.jetbrains.annotations.NotNull;

public class StorageManager {
    private final StorageExecutor executor;

    public StorageManager(@NotNull Configs config) throws StorageLoaderException {
        if (config.getString("storage.type").equalsIgnoreCase("mysql") ||
                config.getString("storage.type").equalsIgnoreCase("mariadb")) {
            executor = new MySQLAdapter(config);
        } else {
            throw new StorageLoaderException("Unsupported storage type");
        }
        try {
            if (executor.checkStorages()) {
                if (Main.DEBUG) {
                    Main.getLogger().info("Storage loaded");
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
