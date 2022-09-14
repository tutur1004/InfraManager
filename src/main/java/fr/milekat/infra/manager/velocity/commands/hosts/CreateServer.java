package fr.milekat.infra.manager.velocity.commands.hosts;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.milekat.infra.manager.api.classes.Game;
import fr.milekat.infra.manager.api.classes.User;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.utils.McTools;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateServer implements SimpleCommand {
    /**
     * /host-admin-create <game name> [<user name>]
     */
    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission("host.admin.server.create");
    }

    @Override
    public void execute(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        if (invocation.source() instanceof Player) {
            Player sender = (Player) invocation.source();
            try {
                if (args.length==1) {
                    Game game = Main.getStorage().getGame(args[0]);
                    if (game==null || !game.isEnable()) {
                        sender.sendMessage(Component.text("§cThis game is invalid or disable."));
                        return;
                    }
                    User user = Main.getStorage().getUser(sender.getUniqueId());
                    if (user==null) {
                        sender.sendMessage(Component.text("§cStorage error, you are not registered."));
                        return;
                    }
                    Main.getHosts().createHost(game, user);
                    Main.getLogger().info(sender.getUsername() + " has created a new host.");
                    sender.sendMessage(Component.text("§aServer created ! Wait 5s for the first start..."));
                } else if (args.length==2) {
                    Game game = Main.getStorage().getGame(args[0]);
                    if (game==null || !game.isEnable()) {
                        sender.sendMessage(Component.text("§cThis game is invalid or disable."));
                        return;
                    }
                    User user = Main.getStorage().getUser(args[1]);
                    if (user==null) {
                        sender.sendMessage(Component.text("§cUser not found."));
                        return;
                    }
                    Main.getHosts().createHost(game, user);
                    Main.getLogger().info(sender.getUsername() + " has created a new host for " + args[1]);
                    sender.sendMessage(Component.text("§aServer created ! Wait 5s for the first start..."));
                } else {
                    sender.sendMessage(Component.text("§c/host-admin-create <game name> [<user name>]"));
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
    public List<String> suggest(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource sender = invocation.source();
        if (args.length <= 1) {
            try {
                String arg = "";
                if (args.length == 1) {
                    arg = args[0];
                }
                return McTools.getTabArgs(arg, Main.getStorage().getGamesCached()
                        .stream()
                        .map(Game::getName)
                        .collect(Collectors.toList()));
            } catch (StorageExecuteException exception) {
                sender.sendMessage(Component.text("§cStorage exception, check console"));
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            }
        }
        return new ArrayList<>();
    }
}
