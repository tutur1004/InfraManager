package fr.milekat.infra.manager.bungeecord.commands.hosts;

import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ResetInfra extends Command {
    /**
     * /host-admin-delete <server name>
     */
    public ResetInfra() {
        super("infra-reset", "infra.admin.reset");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            Main.getUtilsManager().getInfraUtils().resetInfraServerList();
            sender.sendMessage(new TextComponent("Server list reset !"));
        } catch (StorageExecuteException exception) {
            sender.sendMessage(new TextComponent("§cStorage exception, check console"));
            if (Main.DEBUG) {
                exception.printStackTrace();
            }
        }
    }
}
