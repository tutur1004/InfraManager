package fr.milekat.hostmanager.hosts.commands;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
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
            if (args.length==1) {
                try {
                    Instance instance = Main.getStorage().getInstance(args[0]);
                    if (instance==null) {
                        sender.sendMessage(new TextComponent("§cServer not found."));
                        return;
                    }
                    Main.getHosts().deleteServer(instance);
                } catch (StorageExecuteException e) {
                    sender.sendMessage(new TextComponent("§cStorage error."));
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
