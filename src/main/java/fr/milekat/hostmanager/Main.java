package fr.milekat.hostmanager;

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

    private static Plugin plugin;
    private static Configuration configFile;
    public static Boolean DEBUG = false;
    private static StorageManager LOADED_STORAGE;

    @Override
    public void onEnable() {
        plugin = this;
        try {
            configFile = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                    new File(this.getDataFolder(),"config.yml")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    }

    @Override
    public void onDisable() {
        try {
            LOADED_STORAGE.getExecutor().disconnect();
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
    public static StorageExecutor getExecutor() {
        return LOADED_STORAGE.getExecutor();
    }

    /**
     * Get config file
     * @return Config file
     */
    public static Configuration getFileConfig() {
        return configFile;
    }
}