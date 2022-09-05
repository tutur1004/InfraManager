package fr.milekat.hostmanager.bungeecord;

import fr.milekat.hostmanager.bungeecord.commands.BungeeCommands;
import fr.milekat.hostmanager.bungeecord.utils.BungeeUtilsManager;
import fr.milekat.hostmanager.common.Main;
import net.md_5.bungee.api.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainBungee extends Plugin {
    @Override
    @SuppressWarnings("all")
    public void onEnable() {
        Logger logger = LoggerFactory.getLogger("HostManager");
        File configFile = null;
        File targetConfigFile = new File(this.getDataFolder().getPath(), "config.yml");
        if (!targetConfigFile.exists()) {
            try {
                logger.info("Config file not found, trying to create it..");
                File directory = this.getDataFolder();
                if (! directory.exists()){
                    directory.mkdir();
                }
                Files.copy(getClass().getResourceAsStream("/config.yml"),
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
