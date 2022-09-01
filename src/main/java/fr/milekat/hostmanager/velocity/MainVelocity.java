package fr.milekat.hostmanager.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.velocity.commands.VelocityCommands;
import fr.milekat.hostmanager.velocity.utils.VelocityUtilsManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "host-manager",
        name = "HostManager",
        version = "1.0",
        description = "Management plugin of this host system",
        authors = {"MileKat"})
public class MainVelocity {
    @Inject
    @SuppressWarnings("all")
    public MainVelocity(ProxyServer server,
                        CommandManager commandManager,
                        @NotNull Logger logger,
                        @DataDirectory final Path dataFolder) {

        InputStream configFile = null;
        try {
            configFile = Files.newInputStream(new File(dataFolder.toString(), "config.yml").toPath());
        } catch (IOException ignore) {
            logger.warn("Can't load config.yml file, disabling plugin..");
        }
        if (configFile==null) {
            logger.warn("");
        } else {
            new Main(logger, configFile, new VelocityUtilsManager(server));
            new VelocityCommands(commandManager);
        }
    }
}
