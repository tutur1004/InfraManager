package fr.milekat.infra.manager.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.utils.Utils;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.manager.common.utils.InfraUtils;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class VelocityInfra implements InfraUtils {
    private final ProxyServer server;

    public VelocityInfra(ProxyServer server) {
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
    @Override
    public void reconnectAllPlayersToLobby(Instance instance) {
        server.getServer(instance.getName()).ifPresent(registeredServer -> registeredServer.getPlayersConnected().
                forEach(player -> player.createConnectionRequest(getLobbies().get(0))));
    }

    /**
     * Load all hosts in proxy server list
     */
    @Override
    public void resetInfraServerList() throws StorageExecuteException {
        removeServersPrefix(Main.HOST_PREFIX);
        // TODO: 17/09/2022 Check lobby removeServersPrefix(Main.LOBBY_PREFIX);
        for (Instance instance : Main.getStorage().getActiveInstances()) {
            addServer(instance.getName(), instance.getHostname(), instance.getPort());
        }
    }

    /**
     * Add a server to velocity server list
     */
    @Override
    public void addServer(String name, String hostname, int port) {
        InetSocketAddress ipAddress = new InetSocketAddress(hostname, port);
        server.registerServer(new ServerInfo(name, ipAddress));
    }

    /**
     * Remove a server from velocity server list (If exist)
     */
    @Override
    public void removeServer(String name) {
        Optional<RegisteredServer> serverOptional = server.getServer(name);
        serverOptional.ifPresent(registeredServer -> server.unregisterServer(registeredServer.getServerInfo()));
    }

    /**
     * Delete all server that start with prefix
     * @param prefix of the servers to delete
     */
    @Override
    public void removeServersPrefix(String prefix) {
        server.getAllServers()
                .stream()
                .filter(registeredServer -> registeredServer.getServerInfo().getName().startsWith(prefix))
                .forEach(registeredServer -> server.unregisterServer(registeredServer.getServerInfo()));
    }

    @Override
    public void sendPlayerMessage(UUID uuid, String message) {
        server.getPlayer(uuid).ifPresent(player -> player.sendMessage(Component.text(message)));
    }

    @Override
    public void sendPlayerToServer(UUID uuid, String serverName) {
        server.getPlayer(uuid).ifPresent(player -> {
            if (server.getServer(serverName).isPresent()) {
                player.createConnectionRequest(server.getServer(serverName).get()).connect();
            }
        });
    }

    @Override
    public boolean playerIsConnected(UUID uuid) {
        return server.getPlayer(uuid).isPresent();
    }

    @Override
    public boolean playerIsInLobby(UUID uuid) {
        if (server.getPlayer(uuid).isPresent()) {
            Player player = server.getPlayer(uuid).get();
             if (player.getCurrentServer().isPresent()) {
                 return player.getCurrentServer().get().getServerInfo().getName().startsWith(Main.LOBBY_PREFIX);
             } else return false;
        } else return false;
    }
}
