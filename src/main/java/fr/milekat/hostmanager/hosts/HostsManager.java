package fr.milekat.hostmanager.hosts;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.hosts.adapter.pterodactyl.PterodactylAdapter;
import fr.milekat.hostmanager.hosts.commands.CreateServer;
import fr.milekat.hostmanager.hosts.commands.DeleteServer;
import fr.milekat.hostmanager.hosts.commands.ResetHosts;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.hosts.workers.HostRemover;
import fr.milekat.hostmanager.hosts.workers.LobbyChannels;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;

public class HostsManager {
    private final HostExecutor hostExecutor;

    public HostsManager(Plugin plugin, @NotNull Configuration config) throws HostExecuteException {
        //  Load host provider
        if (config.getString("host.settings.provider").equalsIgnoreCase("pterodactyl")) {
            hostExecutor = new PterodactylAdapter();
        } else {
            throw new HostExecuteException("Unsupported database type");
        }
        if (!hostExecutor.checkAvailability()) {
            throw new HostExecuteException("Storages are not loaded properly");
        }
        try {
            Utils.resetBungeeHostList();
        } catch (StorageExecuteException e) {
            Main.getHostLogger().warning("Couldn't load existing hosts to bungee");
        }
        //  Register admin commands
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new CreateServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new DeleteServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new ResetHosts());
        //  Init PluginMessages channels
        new LobbyChannels();
        new HostRemover();
    }

    public HostExecutor getHostExecutor() {
        return hostExecutor;
    }
}