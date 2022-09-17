package fr.milekat.infra.manager.bungeecord.utils;

import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.utils.Utils;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.manager.common.utils.InfraUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeeInfra implements InfraUtils {
    /**
     * Find all bungee server from the config lobby list
     * @return a list of Bungee {@link ServerInfo} representing all lobby
     */
    public static List<ServerInfo> getLobbies() {
        List<String> lobbyList = ProxyServer.getInstance().getServers().keySet()
                .stream()
                .filter(serverName -> Utils.getLobbyList().contains(serverName))
                .collect(Collectors.toList());
        return lobbyList.stream()
                .map(lobby -> ProxyServer.getInstance().getServerInfo(lobby))
                .collect(Collectors.toList());
    }

    /**
     * Reconnect all players from the instance into a lobby
     * @param instance server instance
     */
    public void reconnectAllPlayersToLobby(Instance instance) {
        ProxyServer.getInstance().getServerInfo(instance.getName()).getPlayers()
                .forEach(player -> player.connect(getLobbies().get(0), ServerConnectEvent.Reason.PLUGIN));
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
     * Add a server to bungee server list
     */
    @Override
    public void addServer(String name, String hostname, int port) {
        InetSocketAddress ipAddress = new InetSocketAddress(hostname, port);
        ServerInfo serverInfo = ProxyServer.getInstance()
                .constructServerInfo(name, ipAddress, null, false);
        ProxyServer.getInstance().getServers().put(name, serverInfo);
    }

    /**
     * Remove a server from bungee server list (If exist)
     */
    @Override
    public void removeServer(String name) {
        ProxyServer.getInstance().getServers().remove(name);
    }

    /**
     * Delete all server that start with prefix
     * @param prefix of the servers to delete
     */
    @Override
    public void removeServersPrefix(String prefix) {
        ProxyServer.getInstance().getServers().keySet()
                .stream()
                .filter(serverName -> serverName.startsWith(prefix))
                .collect(Collectors.toSet())
                .forEach(this::removeServer);
    }

    @Override
    public void sendPlayerMessage(UUID uuid, String message) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player!=null) {
            player.sendMessage(new TextComponent(message));
        }
    }

    @Override
    public void sendPlayerToServer(UUID uuid, String serverName) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
            if (player!=null && serverInfo!=null) {
            player.connect(serverInfo);
        }
    }

    @Override
    public boolean playerIsConnected(UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid)!=null;
    }

    @Override
    public boolean playerIsInLobby(UUID uuid) {
        if (playerIsConnected(uuid)) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            return player.getServer().getInfo().getName().startsWith(Main.LOBBY_PREFIX);
        } else return false;
    }
}
