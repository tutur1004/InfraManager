package fr.milekat.hostmanager.velocity.commands.hosts;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import net.kyori.adventure.text.Component;

public class DeleteServer implements SimpleCommand {
    /**
     * /host-admin-delete <server name>
     */
    @Override
    public boolean hasPermission(SimpleCommand.Invocation invocation) {
        return invocation.source().hasPermission("host.admin.server.delete");
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (invocation.source() instanceof Player) {
            Player sender = (Player) invocation.source();
            try {
                sender.sendMessage(Component.text("Trying to delete server..."));
                if (args.length==1) {
                    try {
                        Instance instance = Main.getStorage().getInstance(args[0]);
                        if (instance==null) {
                            sender.sendMessage(Component.text("§cServer not found."));
                            return;
                        }
                        sender.sendMessage(Component.text("Server id: " + instance.getServerId()));
                        Main.getHosts().deleteHost(instance);
                        sender.sendMessage(Component.text("Server deleted."));
                    } catch (StorageExecuteException exception) {
                        sender.sendMessage(Component.text("§cStorage error."));
                        if (Main.DEBUG) {
                            exception.printStackTrace();
                        }
                    }
                } else {
                    sender.sendMessage(Component.text("§c/host-admin-delete <server name>"));
                }
            } catch (HostExecuteException exception) {
                sender.sendMessage(Component.text("§cHost provider exception, check console"));
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
