package fr.milekat.hostmanager.hosts;

import fr.milekat.hostmanager.hosts.commands.CreateServer;
import fr.milekat.hostmanager.hosts.commands.DeleteServer;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.hosts.pterodactyl.PterodactylAdapter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class HostsManager {
    private final HostExecutor hostExecutor;

    public HostsManager(Plugin plugin, Configuration config) throws HostExecuteException {
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new CreateServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new DeleteServer());
        if (config.getString("host.provider").equalsIgnoreCase("pterodactyl")) {
            hostExecutor = new PterodactylAdapter();
        } else {
            throw new HostExecuteException("Unsupported database type");
        }
        if (!hostExecutor.checkAvailability()) {
            throw new HostExecuteException("Storages are not loaded properly");
        }
    }

    public HostExecutor getHostExecutor() {
        return hostExecutor;
    }
}