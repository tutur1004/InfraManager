package fr.milekat.hostmanager.common.hosts;

import fr.milekat.hostmanager.api.classes.Game;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.api.classes.InstanceState;
import fr.milekat.hostmanager.api.classes.User;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.adapter.docker.DockerAdapter;
import fr.milekat.hostmanager.common.hosts.adapter.pterodactyl.PterodactylAdapter;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.common.hosts.workers.HostGarbageCollector;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.common.utils.CommonEvent;
import fr.milekat.hostmanager.common.utils.Configs;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

public class HostsManager {
    private final HostExecutor hostExecutor;

    public HostsManager(@NotNull Configs config) throws HostExecuteException {
        //  Load host provider
        if (config.getString("host.settings.provider").equalsIgnoreCase("pterodactyl")) {
            hostExecutor = new PterodactylAdapter();
        } else if (config.getString("host.settings.provider").equalsIgnoreCase("docker")) {
            hostExecutor = new DockerAdapter();
        } else {
            throw new HostExecuteException("Unsupported database type");
        }
        if (!hostExecutor.checkAvailability()) {
            throw new HostExecuteException("Storages are not loaded properly");
        }
        if (Main.DEBUG) {
            Main.getLogger().info("Host provider: " + config.getString("host.settings.provider") + " loaded !");
        }
        try {
            Main.getUtilsManager().getHostUtils().resetHostList();
        } catch (StorageExecuteException e) {
            Main.getLogger().warn("Couldn't load existing hosts to proxy");
        }
        //  Init messaging feature
        // TODO: 05/09/2022 Messaging feature

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
        instance.setName(Main.HOST_PROXY_SERVER_PREFIX + game.getName() + "-" + instance.getId());
        //  Create the server in the provider
        getHostExecutor().createServer(instance);
        //  Add this new instance to storage
        Main.getStorage().updateInstance(instance);
        //  Add this new instance to the proxy server list
        Main.getUtilsManager().getHostUtils().addServer(instance.getName(), instance.getHostname(), instance.getPort());
        //  Call the ServerCreatedEvent custom event !
        Main.callEvent(CommonEvent.EventName.ServerCreatedEvent, instance);
    }

    public void deleteHost(Instance instance) throws HostExecuteException {
        //  Retrieve this instance in the storage
        try {
            if (Main.getStorage().getActiveInstances()
                    .stream()
                    .noneMatch(o -> o.getServerId().equalsIgnoreCase(instance.getServerId()))
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
        Main.getUtilsManager().getHostUtils().reconnectAllPlayersToLobby(instance);
        Main.getUtilsManager().getHostUtils().removeServer(instance.getName());
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