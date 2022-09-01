package fr.milekat.hostmanager.bungeecord;

import fr.milekat.hostmanager.bungeecord.commands.BungeeCommands;
import fr.milekat.hostmanager.bungeecord.utils.BungeeUtilsManager;
import fr.milekat.hostmanager.common.Main;
import net.md_5.bungee.api.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class MainBungee extends Plugin {
    @Override
    @SuppressWarnings("all")
    public void onEnable() {
        Logger logger = LoggerFactory.getLogger("HostManager");
        InputStream configFile = null;
        try {
            configFile = Files.newInputStream(new File(this.getDataFolder(), "config.yml").toPath());
        } catch (IOException ignore) {
            logger.warn("Can't load config.yml file, disabling plugin..");
            this.onDisable();
        }
        if (configFile==null) {
            this.onDisable();
        } else {
            new Main(logger, configFile, new BungeeUtilsManager(this));
            new BungeeCommands(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            Main.getStorage().disconnect();
        } catch (Exception ignored) {}
    }
}
