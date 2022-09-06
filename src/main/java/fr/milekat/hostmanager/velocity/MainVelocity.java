package fr.milekat.hostmanager.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.velocity.commands.VelocityCommands;
import fr.milekat.hostmanager.velocity.listeners.PlayerJoin;
import fr.milekat.hostmanager.velocity.utils.VelocityUtilsManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Plugin(id = "host-manager",
        name = "HostManager",
        version = "1.0",
        description = "Management plugin of this host system",
        authors = {"MileKat"})
public class MainVelocity {
    private final ProxyServer server;
    private final File configFile;
    private final Logger logger;
    @Inject
    public MainVelocity(ProxyServer server,
                        CommandManager commandManager,
                        @NotNull Logger logger,
                        @DataDirectory final Path dataFolder) {
        this.server = server;
        this.logger = logger;
        File targetConfigFile = new File(dataFolder.toString(), "config.yml");
        if (!targetConfigFile.exists()) {
            try {
                logger.info("Config file not found, trying to create it..");
                File directory = new File(dataFolder.toString());
                if (! directory.exists()){
                    if (!directory.mkdir()) {
                        logger.trace("Can't create config folder.");
                    }
                }
                Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/config.yml")),
                        targetConfigFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException exception) {
                logger.trace("Can't generate default config file.");
                exception.printStackTrace();
            }
        }
        configFile = new File(dataFolder.toString(), "config.yml");
        new VelocityCommands(commandManager);
    }

    @Subscribe
    @SuppressWarnings("all")
    public void onProxyInitialization(ProxyInitializeEvent event) {
        new Main(logger, configFile, new VelocityUtilsManager(this, server));
        server.getEventManager().register(this, new PlayerJoin());
    }
}
