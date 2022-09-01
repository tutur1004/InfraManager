package fr.milekat.hostmanager.velocity.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.Utils;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.common.utils.ServerUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VelocityServer implements ServerUtils {
    private final ProxyServer server;

    public VelocityServer(ProxyServer server) {
        this.server = server;
    }

    /**
     * Get all velocity lobby servers
     * @return list of lobby as velocity servers object
     */
    private List<RegisteredServer> getLobbies() {
        return server.getAllServers()
                .stream()
                .filter(registeredServer -> Utils.getLobbyList().contains(registeredServer.getServerInfo().getName()))
                .collect(Collectors.toList());
    }

    /**
     * Reconnect all players from the instance into a lobby
     * @param instance server instance
     */
    public void reconnectAllPlayersToLobby(Instance instance) {
        server.getServer(instance.getName()).ifPresent(registeredServer -> registeredServer.getPlayersConnected().
                forEach(player -> player.createConnectionRequest(getLobbies().get(0))));
    }

    /**
     * Load all hosts in proxy server list
     */
    public void resetHostList() throws StorageExecuteException {
        removeServersPrefix(Main.HOST_PROXY_SERVER_PREFIX);
        for (Instance server : Main.getStorage().getActiveInstances()) {
            addServer(Main.HOST_PROXY_SERVER_PREFIX + server.getName(), server.getPort());
        }
    }

    /**
     * Add a server to velocity server list
     */
    public void addServer(String name, int port) {
        InetSocketAddress ipAddress =
                new InetSocketAddress(Main.getConfig().getString("host.settings.host"), port);
        server.registerServer(new ServerInfo(name, ipAddress));
    }

    /**
     * Remove a server from velocity server list (If exist)
     */
    public void removeServer(String name) {
        Optional<RegisteredServer> serverOptional = server.getServer(name);
        serverOptional.ifPresent(registeredServer -> server.unregisterServer(registeredServer.getServerInfo()));
    }

    /**
     * Delete all server that start with prefix
     * @param prefix of the servers to delete
     */
    public void removeServersPrefix(String prefix) {
        server.getAllServers()
                .stream()
                .filter(registeredServer -> registeredServer.getServerInfo().getName().startsWith(prefix))
                .forEach(registeredServer -> server.unregisterServer(registeredServer.getServerInfo()));
    }
}
