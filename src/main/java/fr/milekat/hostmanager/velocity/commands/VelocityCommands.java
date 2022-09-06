package fr.milekat.hostmanager.velocity.commands;

import com.velocitypowered.api.command.CommandManager;
import fr.milekat.hostmanager.velocity.commands.hosts.CreateServer;
import fr.milekat.hostmanager.velocity.commands.hosts.DeleteServer;
import fr.milekat.hostmanager.velocity.commands.hosts.ResetHosts;

public class VelocityCommands {
    public VelocityCommands(CommandManager commandManager) {
        commandManager.register(commandManager.metaBuilder("host-admin-create").build(), new CreateServer());
        commandManager.register(commandManager.metaBuilder("host-admin-delete").build(), new DeleteServer());
        commandManager.register(commandManager.metaBuilder("host-reset").build(), new ResetHosts());
    }
}
