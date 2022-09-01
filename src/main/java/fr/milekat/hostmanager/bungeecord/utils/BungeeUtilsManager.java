package fr.milekat.hostmanager.bungeecord.utils;

import fr.milekat.hostmanager.common.utils.UtilsManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeUtilsManager implements UtilsManager {
    private final Plugin plugin;

    public BungeeUtilsManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public BungeeInstanceEvent getInstanceEvent() {
        return new BungeeInstanceEvent();
    }

    public BungeeScheduler getScheduler() {
        return new BungeeScheduler(plugin);
    }

    public BungeeServer getServerManager() {
        return new BungeeServer();
    }
}
