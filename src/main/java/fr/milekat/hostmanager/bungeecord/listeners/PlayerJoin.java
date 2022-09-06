package fr.milekat.hostmanager.bungeecord.listeners;

import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(@NotNull PostLoginEvent event) throws StorageExecuteException {
        if (event.getPlayer().isConnected()) {
            Main.getStorage().updateUser(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        }
    }
}
