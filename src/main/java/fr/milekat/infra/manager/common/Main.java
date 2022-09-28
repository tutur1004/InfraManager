package fr.milekat.infra.manager.common;

import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.hosts.HostsManager;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.messaging.Messaging;
import fr.milekat.infra.manager.common.messaging.MessagingManager;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingLoaderException;
import fr.milekat.infra.manager.common.storage.Storage;
import fr.milekat.infra.manager.common.storage.StorageImplementation;
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
    public static final String PROXY_PREFIX = "proxy";
    public static final String LOBBY_PREFIX = "lobby";
    public static final String HOST_PREFIX = "host";
    public static int PORT = 25565;

    public static Boolean DEBUG = false;
    private static Configs config;
    private static Logger mainLogger;
    private static Storage LOADED_STORAGE;
    private static MessagingManager LOADED_MESSAGING;
    private static HostsManager LOADED_HOSTS_MANAGER;
    private static UtilsManager utilsManagers;

    public Main(int port, Logger logger, File configFile, UtilsManager utilsManager) {
        PORT = port;
        mainLogger = logger;
        utilsManagers = utilsManager;
        config = new Configs(configFile);

        DEBUG = config.getBoolean("debug");
        if (DEBUG) getLogger().info("Debug enable");
        try {
            LOADED_STORAGE = new Storage(config);
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
            LOADED_MESSAGING = new MessagingManager(config);
        } catch (MessagingLoaderException exception) {
            getLogger().warn("Messaging load failed, disabling plugin..");
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
     * Get Storage
     * @return Storage implementation
     */
    public static StorageImplementation getStorage() {
        return LOADED_STORAGE.getStorageImplementation();
    }

    /**
     * Get Messaging
     * @return Messaging interface
     */
    public static Messaging getMessaging() {
        return LOADED_MESSAGING.getMessaging();
    }

    /**
     * Get Host manager
     * @return Host manager
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
    public static UtilsManager getUtils() {
        return utilsManagers;
    }
}
