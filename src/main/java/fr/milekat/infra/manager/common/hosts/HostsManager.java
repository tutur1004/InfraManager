package fr.milekat.infra.manager.common.hosts;

import fr.milekat.infra.manager.api.classes.Game;
import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.api.classes.InstanceState;
import fr.milekat.infra.manager.api.classes.User;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.adapter.docker.DockerAdapter;
import fr.milekat.infra.manager.common.hosts.adapter.pterodactyl.PterodactylAdapter;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.hosts.utils.HostGarbageCollector;
import fr.milekat.infra.manager.common.hosts.utils.Utils;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.manager.common.utils.CommonEvent;
import fr.milekat.infra.manager.common.utils.Configs;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

public class HostsManager {
    private final HostExecutor hostExecutor;

    public HostsManager(@NotNull Configs config) throws HostExecuteException {
        //  Load host provider
        String hostProvider = config.getString("host.settings.provider");
        if (Main.DEBUG) {
            Main.getLogger().info("Loading storage type: " + hostProvider);
        }
        if (hostProvider.equalsIgnoreCase("pterodactyl")) {
            hostExecutor = new PterodactylAdapter();
        } else if (hostProvider.equalsIgnoreCase("docker")) {
            hostExecutor = new DockerAdapter();
        } else {
            throw new HostExecuteException("Unsupported database type");
        }
        if (!hostExecutor.checkAvailability()) {
            throw new HostExecuteException("Storages are not loaded properly");
        }
        if (Main.DEBUG) {
            Main.getLogger().info("Host provider: " + hostProvider + " loaded !");
        }
        try {
            Main.getUtils().getInfraUtils().resetInfraServerList();
        } catch (StorageExecuteException e) {
            Main.getLogger().warn("Couldn't load existing hosts to proxy");
        }

        //  Worker to remove "ghost"/"unused" hosts
        new HostGarbageCollector();
    }

    private HostExecutor getHostExecutor() {
        return hostExecutor;
    }

    public void createHost(Game game, User user) throws HostExecuteException, StorageExecuteException {
        Instance instance = new Instance(UUID.randomUUID().toString(), game, user);
        Integer port = Utils.getAvailablePort();
        if (port!=null) {
            instance.setPort(port);
        } else {
            Main.getLogger().warn("No port available.");
            throw new HostExecuteException("No port available.");
        }
        instance.setState(InstanceState.CREATING);
        //  Add this new instance to storage
        instance = Main.getStorage().createInstance(instance);
        //  Update the name with SQL id and game name
        instance.setName(Main.HOST_PREFIX + "-" + game.getName() + "-" + instance.getId());
        //  Create the server in the provider
        getHostExecutor().createServer(instance);
        //  Add this new instance to storage
        Main.getStorage().updateInstance(instance);
        //  Add this new instance to the proxy server list
        Main.getUtils().getInfraUtils().addServer(instance.getName(), instance.getHostname(), instance.getPort());
        //  Call the ServerCreatedEvent custom event !
        Main.callEvent(CommonEvent.EventName.ServerCreatedEvent, instance);
    }

    public void deleteHost(Instance instance) throws HostExecuteException {
        //  Retrieve this instance in the storage
        try {
            if (Main.getStorage().getActiveInstances()
                    .stream()
                    .filter(activeInstance -> activeInstance.getServerId()!=null)
                    .noneMatch(activeInstance -> activeInstance.getServerId().equalsIgnoreCase(instance.getServerId()))
            ) {
                return;
            }
        } catch (StorageExecuteException exception) {
            if (Main.DEBUG) {
                Main.getLogger().warn("Trying to delete an invalid or inactive server");
            }
            return;
        }
        //  Call ServerDeletionEvent and cancel the deletion if the event is cancelled !
        CommonEvent deletionEvent = Main.callEvent(CommonEvent.EventName.ServerDeletionEvent, instance);
        if (deletionEvent.isCancelled()) return;
        //  Reconnect all players from the host to the lobby, and delete the host
        Main.getUtils().getInfraUtils().reconnectAllPlayersToLobby(instance);
        Main.getUtils().getInfraUtils().removeServer(instance.getName());
        //  Delete the server from the provider
        getHostExecutor().deleteServer(instance);
        //  Update this instance in the storage
        try {
            instance.setState(InstanceState.TERMINATED);
            instance.setDeletion(new Date());
            Main.getStorage().updateInstance(instance);
        } catch (StorageExecuteException exception) {
            throw new HostExecuteException(exception, "Can't update storage after server deletion");
        }
        //  Call the ServerDeletedEvent custom event !
        Main.callEvent(CommonEvent.EventName.ServerDeletedEvent, instance);
    }
}