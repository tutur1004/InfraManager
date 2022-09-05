package fr.milekat.hostmanager.bungeecord.utils;

import fr.milekat.hostmanager.common.utils.EventCaller;
import fr.milekat.hostmanager.common.utils.HostUtils;
import fr.milekat.hostmanager.common.utils.Scheduler;
import fr.milekat.hostmanager.common.utils.UtilsManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeUtilsManager implements UtilsManager {
    private final Plugin plugin;

    public BungeeUtilsManager(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Get interface InstanceEvent Bungee
     * @return {@link EventCaller}
     */
    @Override
    public BungeeInstanceEvent getInstanceEvent() {
        return new BungeeInstanceEvent();
    }

    /**
     * Get interface Scheduler Bungee
     * @return {@link Scheduler}
     */
    @Override
    public BungeeScheduler getScheduler() {
        return new BungeeScheduler(plugin);
    }

    /**
     * Get interface ServerManager Bungee
     * @return {@link HostUtils}
     */
    @Override
    public BungeeHost getHostUtils() {
        return new BungeeHost();
    }
}
