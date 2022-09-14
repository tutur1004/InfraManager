package fr.milekat.infra.manager.bungeecord.commands;

import fr.milekat.infra.manager.bungeecord.commands.hosts.CreateServer;
import fr.milekat.infra.manager.bungeecord.commands.hosts.DeleteServer;
import fr.milekat.infra.manager.bungeecord.commands.hosts.ResetInfra;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCommands {
    public BungeeCommands(Plugin plugin) {
        //  Register admin commands
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new CreateServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new DeleteServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, new ResetInfra());
    }
}
