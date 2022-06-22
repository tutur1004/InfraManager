package fr.milekat.hostmanager.hosts.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class ServerManager {
    // TODO: 22/06/2022 Load servers in bungee list on startup
    /**
     * Add a server to bungee server list
     */
    public static void addServer(String name, int port) {
        InetSocketAddress ipAddress = new InetSocketAddress("172.18.0.1", port);
        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(name, ipAddress, null, false);
        ProxyServer.getInstance().getServers().put(name, serverInfo);
    }

    public static void removeServer(String name) {
        ProxyServer.getInstance().getServers().remove(name);
    }
}
