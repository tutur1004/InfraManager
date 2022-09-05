package fr.milekat.hostmanager.velocity.commands.hosts;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.milekat.hostmanager.api.classes.Game;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.api.classes.User;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.utils.McTools;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.stream.Collectors;

public class CreateServer implements SimpleCommand {
    /**
     * /host-admin-create <server name> <game name> [<user name>]
     */
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("host.admin.server.create");
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (invocation.source() instanceof Player) {
            Player sender = (Player) invocation.source();
            try {
                if (args.length==2) {
                    Game game = Main.getStorage().getGame(args[1]);
                    if (game==null || !game.isEnable()) {
                        sender.sendMessage(Component.text("§cThis game is invalid or disable."));
                        return;
                    }
                    User host = Main.getStorage().getUser(sender.getUniqueId());
                    if (host==null) {
                        sender.sendMessage(Component.text("§cStorage error, you are not registered."));
                        return;
                    }
                    Main.getHosts().createHost(new Instance(args[0], game, host));
                    Main.getLogger().info(sender.getUsername() + " has created a new host.");
                    sender.sendMessage(Component.text("§aServer created ! Wait 5s for the first start..."));
                } else if (args.length==3) {
                    Game game = Main.getStorage().getGame(args[1]);
                    if (game==null || !game.isEnable()) {
                        sender.sendMessage(Component.text("§cThis game is invalid or disable."));
                        return;
                    }
                    User host = Main.getStorage().getUser(args[2]);
                    if (host==null) {
                        sender.sendMessage(Component.text("§cUser not found."));
                        return;
                    }
                    Main.getHosts().createHost(new Instance(args[0], game, host));
                    Main.getLogger().info(sender.getUsername() + " has created a new host for " + args[2]);
                    sender.sendMessage(Component.text("§aServer created ! Wait 5s for the first start..."));
                } else {
                    sender.sendMessage(Component.text("§c/host-admin-create <server name> <game name> [<user name>]"));
                }
            } catch (StorageExecuteException exception) {
                sender.sendMessage(Component.text("§cStorage exception, check console"));
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            } catch (HostExecuteException exception) {
                sender.sendMessage(Component.text("§cHost provider exception, check console"));
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource sender = invocation.source();
        if (args.length>=2) {
            try {
                return McTools.getTabArgs(args[1], Main.getStorage().getGamesCached().stream()
                        .map(Game::getName).collect(Collectors.toList()));
            } catch (StorageExecuteException exception) {
                sender.sendMessage(Component.text("§cStorage exception, check console"));
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            }
        }
        return null;
    }
}
