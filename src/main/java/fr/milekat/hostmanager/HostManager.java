package fr.milekat.hostmanager;

import fr.milekat.hostmanager.storage.StorageExecutor;
import fr.milekat.hostmanager.storage.StorageManager;
import fr.milekat.hostmanager.storage.exeptions.StorageLoaderException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class HostManager extends JavaPlugin {
    private static JavaPlugin plugin;
    public static Boolean DEBUG = false;
    private static StorageManager LOADED_STORAGE;

    @Override
    public void onEnable() {
        plugin = this;
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!(configFile.exists())) {
            this.saveResource("config.yml", false);
        }
        DEBUG = getConfig().getBoolean("debug");
        if (DEBUG) getHostLogger().info("Debug enable");
        try {
            LOADED_STORAGE = new StorageManager(this.getConfig());
            if (DEBUG) {
                getHostLogger().info("Storage enable, API is now available");
            }
        } catch (StorageLoaderException throwable) {
            getHostLogger().warning("Storage load failed, disabling plugin..");
            getServer().getPluginManager().disablePlugin(this);
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
}
