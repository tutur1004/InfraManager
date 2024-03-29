package fr.milekat.infra.manager.bungeecord;

import fr.milekat.infra.manager.bungeecord.commands.BungeeCommands;
import fr.milekat.infra.manager.bungeecord.listeners.PlayerJoin;
import fr.milekat.infra.manager.bungeecord.utils.BungeeUtilsManager;
import fr.milekat.infra.manager.common.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class MainBungee extends Plugin {
    @Override
    @SuppressWarnings("all")
    public void onEnable() {
        int port = ((InetSocketAddress) ProxyServer.getInstance().getConfig()
                .getListeners().iterator().next().getSocketAddress()).getPort();
        Logger logger = LoggerFactory.getLogger("InfraManager");
        File configFile = null;
        File targetConfigFile = new File(this.getDataFolder().getPath(), "config.yml");
        if (!targetConfigFile.exists()) {
            try {
                logger.info("Config file not found, trying to create it..");
                File directory = this.getDataFolder();
                if (! directory.exists()){
                    directory.mkdir();
                }
                Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/config.yml")),
                        targetConfigFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException exception) {
                logger.trace("Can't generate default config file.");
                exception.printStackTrace();
                this.onDisable();
            }
        }
        configFile = new File(this.getDataFolder().getPath(), "config.yml");
        if (configFile==null) {
            this.onDisable();
        } else {
            new Main(port, logger, configFile, new BungeeUtilsManager(this));
            new BungeeCommands(this);
            getProxy().getPluginManager().registerListener(this, new PlayerJoin());
        }
    }

    @Override
    public void onDisable() {
        try {
            Main.getStorage().disconnect();
        } catch (Exception ignored) {}
    }
}
