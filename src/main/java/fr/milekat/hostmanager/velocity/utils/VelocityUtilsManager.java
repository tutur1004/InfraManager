package fr.milekat.hostmanager.velocity.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import fr.milekat.hostmanager.common.utils.UtilsManager;

public class VelocityUtilsManager implements UtilsManager {
    private final ProxyServer server;

    public VelocityUtilsManager(ProxyServer server) {
        this.server = server;
    }

    public VelocityInstanceEvent getInstanceEvent() {
        return new VelocityInstanceEvent(server);
    }

    public VelocityScheduler getScheduler() {
        return new VelocityScheduler(server);
    }

    public VelocityServer getServerManager() {
        return new VelocityServer(server);
    }
}
