package fr.milekat.hostmanager.bungeecord.commands.hosts;

import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ResetHosts extends Command {
    /**
     * /host-admin-delete <server name>
     */
    public ResetHosts() {
        super("host-reset", "host.admin.reset");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            Main.getUtilsManager().getHostUtils().resetHostList();
            sender.sendMessage(new TextComponent("Server list reset !"));
        } catch (StorageExecuteException exception) {
            sender.sendMessage(new TextComponent("§cStorage exception, check console"));
            if (Main.DEBUG) {
                exception.printStackTrace();
            }
        }
    }
}
