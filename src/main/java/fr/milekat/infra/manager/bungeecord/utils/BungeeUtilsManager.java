package fr.milekat.infra.manager.bungeecord.utils;

import fr.milekat.infra.manager.common.utils.EventCaller;
import fr.milekat.infra.manager.common.utils.InfraUtils;
import fr.milekat.infra.manager.common.utils.Scheduler;
import fr.milekat.infra.manager.common.utils.UtilsManager;
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
     * @return {@link InfraUtils}
     */
    @Override
    public BungeeInfra getInfraUtils() {
        return new BungeeInfra();
    }
}
