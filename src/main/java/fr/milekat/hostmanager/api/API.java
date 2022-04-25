package fr.milekat.hostmanager.api;

import fr.milekat.hostmanager.HostManager;
import fr.milekat.hostmanager.storage.StorageExecutor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class API {
    private static final StorageExecutor EXECUTOR = HostManager.getExecutor();

    /**
     * Get tickets amount of player
     * @param uuid uuid of player
     * @return ticket amount
     */
    public static Integer getTickets(UUID uuid) {
        return EXECUTOR.getTicket(uuid);
    }

    /**
     * Get tickets amount of player
     * @param player player
     * @return ticket amount
     */
    public static Integer getTickets(Player player) {
        return getTickets(player.getUniqueId());
    }
}
