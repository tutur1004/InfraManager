package fr.milekat.infra.manager.velocity.commands.hosts;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import net.kyori.adventure.text.Component;

public class ResetHosts implements SimpleCommand {
    /**
     * /host-admin-delete <server name>
     */
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("host.admin.reset");
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player) {
            Player sender = (Player) invocation.source();
            try {
                Main.getUtilsManager().getHostUtils().resetHostList();
                sender.sendMessage(Component.text("Server list reset !"));
            } catch (StorageExecuteException exception) {
                sender.sendMessage(Component.text("§cStorage exception, check console"));
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
