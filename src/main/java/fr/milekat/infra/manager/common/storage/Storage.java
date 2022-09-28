package fr.milekat.infra.manager.common.storage;

import fr.milekat.infra.manager.api.classes.Game;
import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.api.classes.User;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.storage.adapter.sql.SQLStorage;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.manager.common.storage.exeptions.StorageLoaderException;
import fr.milekat.infra.manager.common.utils.Configs;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Storage {    public static final long GAMES_DELAY = TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES);
    public static Map<Game, Date> GAMES_CACHE = new HashMap<>();
    public static final long INSTANCES_DELAY = TimeUnit.MILLISECONDS.convert(2L, TimeUnit.SECONDS);
    public static Map<Instance, Date> INSTANCES_CACHE = new HashMap<>();
    public static final long USERS_DELAY = TimeUnit.MILLISECONDS.convert(1L, TimeUnit.SECONDS);
    public static Map<User, Date> USERS_CACHE = new HashMap<>();

    private final StorageImplementation executor;

    public Storage(@NotNull Configs config) throws StorageLoaderException {
        String storageType = config.getString("storage.type");
        if (Main.DEBUG) {
            Main.getLogger().info("Loading storage type: " + storageType);
        }
        switch (storageType.toLowerCase()) {
            case "mysql":
            case "mariadb":
            case "postgres": {
                executor = new SQLStorage(config);
                break;
            }
            default: throw new StorageLoaderException("Unsupported storage type");
        }
        try {
            if (executor.checkStorages()) {
                if (Main.DEBUG) {
                    Main.getLogger().info("Storage loaded with provider '" + executor.getImplementationName() + "'.");
                }
            } else {
                throw new StorageLoaderException("Storages are not loaded properly");
            }
        } catch (StorageExecuteException exception) {
            throw new StorageLoaderException("Can't load storage properly");
        }

    }

    public StorageImplementation getStorageImplementation() {
        return this.executor;
    }
}
