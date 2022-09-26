package fr.milekat.infra.manager.bungeecord.commands.hosts;

import fr.milekat.infra.manager.api.classes.Game;
import fr.milekat.infra.manager.api.classes.User;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.utils.McTools;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.stream.Collectors;

public class CreateServer extends Command implements TabExecutor {
    /**
     * /host-admin-create <game name> [<user name>]
     */
    public CreateServer() {
        super("host-admin-create", "host.admin.server.create");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length==2) {
                Game game = Main.getStorage().getGame(args[0], args[1]);
                if (game==null || !game.isEnable()) {
                    sender.sendMessage(new TextComponent("§cThis game is invalid or disable."));
                    return;
                }
                User user = Main.getStorage().getUser(((ProxiedPlayer) sender).getUniqueId());
                if (user==null) {
                    sender.sendMessage(new TextComponent("§cStorage error, you are not registered."));
                    return;
                }
                Main.getHosts().createHost(game, user);
                Main.getLogger().info(sender.getName() + " has created a new host.");
                sender.sendMessage(new TextComponent("§aServer created ! Wait 5s for the first start..."));
            } else if (args.length==3) {
                Game game = Main.getStorage().getGame(args[0], args[1]);
                if (game==null || !game.isEnable()) {
                    sender.sendMessage(new TextComponent("§cThis game is invalid or disable."));
                    return;
                }
                User user = Main.getStorage().getUser(args[2]);
                if (user==null) {
                    sender.sendMessage(new TextComponent("§cUser not found."));
                    return;
                }
                Main.getHosts().createHost(game, user);
                Main.getLogger().info(sender.getName() + " has created a new host for " + args[1]);
                sender.sendMessage(new TextComponent("§aServer created ! Wait 5s for the first start..."));
            } else {
                sender.sendMessage(new TextComponent("§c/host-admin-create <game name> [<user name>]"));
            }
        } catch (StorageExecuteException exception) {
            sender.sendMessage(new TextComponent("§cStorage exception, check console"));
            if (Main.DEBUG) {
                exception.printStackTrace();
            }
        } catch (HostExecuteException exception) {
            sender.sendMessage(new TextComponent("§cHost provider exception, check console"));
            if (Main.DEBUG) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            try {
                String arg = "";
                if (args.length == 1) {
                    arg = args[0];
                }
                return McTools.getTabArgs(arg, Main.getStorage().getGamesCached().stream()
                        .map(Game::getName).collect(Collectors.toList()));
            } catch (StorageExecuteException exception) {
                sender.sendMessage(new TextComponent("§cStorage exception, check console"));
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            }
        }
        return null;
    }
}
