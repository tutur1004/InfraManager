package fr.milekat.hostmanager.bungeecord.commands.hosts;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class DeleteServer extends Command {
    /**
     * /host-admin-delete <server name>
     */
    public DeleteServer() {
        super("host-admin-delete", "host.admin.server.delete");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            sender.sendMessage(new TextComponent("Trying to delete server..."));
            if (args.length==1) {
                try {
                    Instance instance = Main.getStorage().getInstance(args[0]);
                    if (instance==null) {
                        sender.sendMessage(new TextComponent("§cServer not found."));
                        return;
                    }
                    sender.sendMessage(new TextComponent("Server id: " + instance.getServerId()));
                    Main.getHosts().deleteHost(instance);
                    sender.sendMessage(new TextComponent("Server deleted."));
                } catch (StorageExecuteException exception) {
                    sender.sendMessage(new TextComponent("§cStorage error."));
                    if (Main.DEBUG) {
                        exception.printStackTrace();
                    }
                }
            } else {
                sender.sendMessage(new TextComponent("§c/host-admin-delete <server name>"));
            }
        } catch (HostExecuteException exception) {
            sender.sendMessage(new TextComponent("§cHost provider exception, check console"));
            if (Main.DEBUG) {
                exception.printStackTrace();
            }
        }
    }
}
