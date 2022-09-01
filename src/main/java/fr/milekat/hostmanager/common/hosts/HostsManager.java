package fr.milekat.hostmanager.common.hosts;

import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.adapter.pterodactyl.PterodactylAdapter;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.common.hosts.workers.HostRemover;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.common.utils.Configs;
import org.jetbrains.annotations.NotNull;

public class HostsManager {
    private final HostExecutor hostExecutor;

    public HostsManager(@NotNull Configs config) throws HostExecuteException {
        //  Load host provider
        if (config.getString("host.settings.provider").equalsIgnoreCase("pterodactyl")) {
            hostExecutor = new PterodactylAdapter();
        //} else if (config.getString("host.settings.provider").equalsIgnoreCase("docker")) {
        //    hostExecutor = new DockerAdapter();
        } else {
            throw new HostExecuteException("Unsupported database type");
        }
        if (!hostExecutor.checkAvailability()) {
            throw new HostExecuteException("Storages are not loaded properly");
        }
        try {
            Main.getUtilsManager().getServerManager().resetHostList();
        } catch (StorageExecuteException e) {
            Main.getLogger().warn("Couldn't load existing hosts to proxy");
        }
        //  Init PluginMessages channels
        //new LobbyChannels();
        new HostRemover();
    }

    public HostExecutor getHostExecutor() {
        return hostExecutor;
    }
}