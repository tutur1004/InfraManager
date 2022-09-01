package fr.milekat.hostmanager.bungeecord.utils;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.Utils;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.hostmanager.common.utils.ServerUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class BungeeServer implements ServerUtils {
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
    public void resetHostList() throws StorageExecuteException {
        removeServersPrefix(Main.HOST_PROXY_SERVER_PREFIX);
        for (Instance server : Main.getStorage().getActiveInstances()) {
            addServer(Main.HOST_PROXY_SERVER_PREFIX + server.getName(), server.getPort());
        }
    }

    /**
     * Add a server to bungee server list
     */
    public void addServer(String name, int port) {
        InetSocketAddress ipAddress =
                new InetSocketAddress(Main.getConfig().getString("host.settings.host"), port);
        ServerInfo serverInfo = ProxyServer.getInstance()
                .constructServerInfo(name, ipAddress, null, false);
        ProxyServer.getInstance().getServers().put(name, serverInfo);
    }

    /**
     * Remove a server from bungee server list (If exist)
     */
    public void removeServer(String name) {
        ProxyServer.getInstance().getServers().remove(name);
    }

    /**
     * Delete all server that start with prefix
     * @param prefix of the servers to delete
     */
    public void removeServersPrefix(String prefix) {
        ProxyServer.getInstance().getServers().keySet()
                .stream()
                .filter(serverName -> serverName.startsWith(prefix))
                .collect(Collectors.toSet())
                .forEach(this::removeServer);
    }
}
