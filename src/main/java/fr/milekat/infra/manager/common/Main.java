package fr.milekat.infra.manager.common;

import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.hosts.HostsManager;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.storage.StorageExecutor;
import fr.milekat.infra.manager.common.storage.StorageManager;
import fr.milekat.infra.manager.common.storage.exeptions.StorageLoaderException;
import fr.milekat.infra.manager.common.utils.CommonEvent;
import fr.milekat.infra.manager.common.utils.Configs;
import fr.milekat.infra.manager.common.utils.Task;
import fr.milekat.infra.manager.common.utils.UtilsManager;
import org.slf4j.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final String HOST_UUID_ENV_VAR_NAME = "HOST_UUID";
    public static final String HOST_PROXY_SERVER_PREFIX = "host-";
    public static final String MESSAGE_CHANNEL = "host:channel";

    public static Boolean DEBUG = false;
    private static Configs config;
    private static Logger mainLogger;
    private static StorageManager LOADED_STORAGE;
    private static HostsManager LOADED_HOSTS_MANAGER;
    private static UtilsManager utilsManagers;

    public Main(Logger logger, File configFile, UtilsManager utilsManager) {
        mainLogger = logger;
        utilsManagers = utilsManager;
        config = new Configs(configFile);

        DEBUG = config.getBoolean("debug");
        if (DEBUG) getLogger().info("Debug enable");
        try {
            LOADED_STORAGE = new StorageManager(config);
            if (DEBUG) {
                getLogger().info("Storage enable, API is now available");
            }
        } catch (StorageLoaderException exception) {
            getLogger().warn("Storage load failed, disabling plugin..");
            if (DEBUG) {
                exception.printStackTrace();
            } else {
                getLogger().warn("Error: " + exception.getLocalizedMessage());
            }
        }
        try {
            LOADED_HOSTS_MANAGER = new HostsManager(config);
            if (DEBUG) {
                getLogger().info("Host manager enable, hosts are now available");
            }
        } catch (HostExecuteException exception) {
            getLogger().warn("Host provider load failed, disabling plugin..");
            if (DEBUG) {
                exception.printStackTrace();
            } else {
                getLogger().warn("Error: " + exception.getLocalizedMessage());
            }
        }
    }

    /**
     * Use plugin logger
     * @return Custom logger
     */
    public static Logger getLogger() {
        return mainLogger;
    }

    /**
     * Get Storage Database Executor
     * @return Storage executor
     */
    public static StorageExecutor getStorage() {
        return LOADED_STORAGE.getStorageExecutor();
    }

    /**
     * Get Storage Database Executor
     * @return Storage executor
     */
    public static HostsManager getHosts() {
        return LOADED_HOSTS_MANAGER;
    }

    /**
     * Get config file
     * @return Config file
     */
    public static Configs getConfig() {
        return config;
    }

    /**
     * Event implementation
     * @return Event
     */
    public static CommonEvent callEvent(CommonEvent.EventName eventName, Instance instance) {
        return utilsManagers.getInstanceEvent().callEvent(eventName, instance);
    }

    /**
     * Schedule a {@link Runnable} task
     * @return Scheduled task
     */
    public static Task schedule(Runnable task, long delay, long period, TimeUnit unit) {
        return utilsManagers.getScheduler().newSchedule(task, delay, period, unit);
    }

    /**
     * Get {@link UtilsManager} proxied
     * @return {@link UtilsManager}
     */
    public static UtilsManager getUtilsManager() {
        return utilsManagers;
    }
}
