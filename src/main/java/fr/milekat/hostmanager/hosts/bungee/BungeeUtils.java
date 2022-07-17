package fr.milekat.hostmanager.hosts.bungee;

import fr.milekat.hostmanager.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;
import java.util.stream.Collectors;

public class BungeeUtils {
    /**
     * Find all bungee server from the config lobby list
     * @return a list of Bungee {@link ServerInfo} representing all lobby
     */
    public static List<ServerInfo> getLobbyList() {
        List<String> lobbyList = Main.getFileConfig().getStringList("host.settings.lobby-name");
        return lobbyList.stream()
                .map(lobby -> ProxyServer.getInstance().getServerInfo(lobby))
                .collect(Collectors.toList());
    }
}
