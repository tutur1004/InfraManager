package fr.milekat.infra.manager.velocity.commands;

import com.velocitypowered.api.command.CommandManager;
import fr.milekat.infra.manager.velocity.commands.hosts.CreateServer;
import fr.milekat.infra.manager.velocity.commands.hosts.DeleteServer;
import fr.milekat.infra.manager.velocity.commands.hosts.ResetInfra;

public class VelocityCommands {
    public VelocityCommands(CommandManager commandManager) {
        commandManager.register(commandManager.metaBuilder("host-admin-create").build(), new CreateServer());
        commandManager.register(commandManager.metaBuilder("host-admin-delete").build(), new DeleteServer());
        commandManager.register(commandManager.metaBuilder("infra-reset").build(), new ResetInfra());
    }
}
