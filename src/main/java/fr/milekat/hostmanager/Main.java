package fr.milekat.hostmanager;

import fr.milekat.hostmanager.hosts.HostExecutor;
import fr.milekat.hostmanager.hosts.HostsManager;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.storage.StorageExecutor;
import fr.milekat.hostmanager.storage.StorageManager;
import fr.milekat.hostmanager.storage.exeptions.StorageLoaderException;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Main extends Plugin {
    public static String HOST_UUID_ENV_VAR_NAME = "HOST_UUID";
    public static String HOST_BUNGEE_SERVER_PREFIX = "host:";

    private static Plugin plugin;
    private static Configuration configFile;
    public static Boolean DEBUG = false;
    private static StorageManager LOADED_STORAGE;
    private static HostsManager LOADED_HOSTS_MANAGER;

    @Override
    public void onEnable() {
        plugin = this;
        try {
            configFile = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                    new File(this.getDataFolder(),"config.yml")
            );
        } catch (IOException ignore) {
            getHostLogger().warning("Can't load config.yml file, disabling plugin..");
            this.onDisable();
        }
        DEBUG = configFile.getBoolean("debug");
        if (DEBUG) getHostLogger().info("Debug enable");
        try {
            LOADED_STORAGE = new StorageManager(configFile);
            if (DEBUG) {
                getHostLogger().info("Storage enable, API is now available");
            }
        } catch (StorageLoaderException throwable) {
            getHostLogger().warning("Storage load failed, disabling plugin..");
            this.onDisable();
            if (DEBUG) {
                throwable.printStackTrace();
            } else {
                getHostLogger().warning("Error: " + throwable.getLocalizedMessage());
            }
        }
        try {
            LOADED_HOSTS_MANAGER = new HostsManager(this, configFile);
            if (DEBUG) {
                getHostLogger().info("Host manager enable, hosts are now available");
            }
        } catch (HostExecuteException throwable) {
            getHostLogger().warning("Host provider load failed, disabling plugin..");
            this.onDisable();
            if (DEBUG) {
                throwable.printStackTrace();
            } else {
                getHostLogger().warning("Error: " + throwable.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onDisable() {
        try {
            LOADED_STORAGE.getStorageExecutor().disconnect();
        } catch (Exception ignored) {}
    }

    /**
     * Use plugin logger
     * @return Custom logger
     */
    public static Logger getHostLogger() {
        return plugin.getLogger();
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
    public static HostExecutor getHosts() {
        return LOADED_HOSTS_MANAGER.getHostExecutor();
    }

    /**
     * Get config file
     * @return Config file
     */
    public static Configuration getFileConfig() {
        return configFile;
    }
}
