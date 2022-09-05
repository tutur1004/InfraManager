package fr.milekat.hostmanager.bungeecord.commands.hosts;

import fr.milekat.hostmanager.api.classes.Game;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.api.classes.User;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import fr.milekat.utils.McTools;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.stream.Collectors;

public class CreateServer extends Command implements TabExecutor {
    /**
     * /host-admin-create <server name> <game name> [<user name>]
     */
    public CreateServer() {
        super("host-admin-create", "host.admin.server.create");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length==2) {
                Game game = Main.getStorage().getGame(args[1]);
                if (game==null || !game.isEnable()) {
                    sender.sendMessage(new TextComponent("§cThis game is invalid or disable."));
                    return;
                }
                User host = Main.getStorage().getUser(((ProxiedPlayer) sender).getUniqueId());
                if (host==null) {
                    sender.sendMessage(new TextComponent("§cStorage error, you are not registered."));
                    return;
                }
                Main.getHosts().createHost(new Instance(args[0], game, host));
                Main.getLogger().info(sender.getName() + " has created a new host.");
                sender.sendMessage(new TextComponent("§aServer created ! Wait 5s for the first start..."));
            } else if (args.length==3) {
                Game game = Main.getStorage().getGame(args[1]);
                if (game==null || !game.isEnable()) {
                    sender.sendMessage(new TextComponent("§cThis game is invalid or disable."));
                    return;
                }
                User host = Main.getStorage().getUser(args[2]);
                if (host==null) {
                    sender.sendMessage(new TextComponent("§cUser not found."));
                    return;
                }
                Main.getHosts().createHost(new Instance(args[0], game, host));
                Main.getLogger().info(sender.getName() + " has created a new host for " + args[2]);
                sender.sendMessage(new TextComponent("§aServer created ! Wait 5s for the first start..."));
            } else {
                sender.sendMessage(new TextComponent("§c/host-admin-create <server name> <game name> [<user name>]"));
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
        if (args.length>=2) {
            try {
                return McTools.getTabArgs(args[1], Main.getStorage().getGamesCached().stream()
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
