package fr.milekat.hostmanager.velocity.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import fr.milekat.hostmanager.common.utils.EventCaller;
import fr.milekat.hostmanager.common.utils.HostUtils;
import fr.milekat.hostmanager.common.utils.Scheduler;
import fr.milekat.hostmanager.common.utils.UtilsManager;
import fr.milekat.hostmanager.velocity.MainVelocity;

public class VelocityUtilsManager implements UtilsManager {
    private final MainVelocity plugin;
    private final ProxyServer server;

    public VelocityUtilsManager(MainVelocity plugin, ProxyServer server) {
        this.plugin = plugin;
        this.server = server;
    }

    /**
     * Get interface InstanceEvent Velocity
     * @return {@link EventCaller}
     */
    @Override
    public VelocityInstanceEvent getInstanceEvent() {
        return new VelocityInstanceEvent(server);
    }

    /**
     * Get interface Scheduler Velocity
     * @return {@link Scheduler}
     */
    @Override
    public VelocityScheduler getScheduler() {
        return new VelocityScheduler(plugin, server);
    }

    /**
     * Get interface ServerManager Velocity
     * @return {@link HostUtils}
     */
    @Override
    public VelocityHost getHostUtils() {
        return new VelocityHost(server);
    }
}
