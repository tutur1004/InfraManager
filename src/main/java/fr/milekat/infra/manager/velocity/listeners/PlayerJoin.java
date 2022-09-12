package fr.milekat.infra.manager.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import org.jetbrains.annotations.NotNull;

public class PlayerJoin {
    @Subscribe
    public void onPlayerJoin(@NotNull PostLoginEvent event) throws StorageExecuteException {
        Player player = event.getPlayer();
        Main.getStorage().updateUser(player.getUniqueId(), player.getUsername());
    }
}
