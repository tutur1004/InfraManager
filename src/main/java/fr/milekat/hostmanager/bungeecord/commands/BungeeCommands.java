package fr.milekat.hostmanager.bungeecord.commands;

import fr.milekat.hostmanager.bungeecord.commands.hosts.CreateServer;
import fr.milekat.hostmanager.bungeecord.commands.hosts.DeleteServer;
import fr.milekat.hostmanager.bungeecord.commands.hosts.ResetHosts;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCommands {
    public BungeeCommands(Plugin plugin) {
        //  Register admin commands
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new CreateServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new DeleteServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new ResetHosts());
    }
}
