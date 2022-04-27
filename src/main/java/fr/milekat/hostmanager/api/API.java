package fr.milekat.hostmanager.api;

import fr.milekat.hostmanager.HostManager;
import fr.milekat.hostmanager.storage.StorageExecutor;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import org.bukkit.entity.Player;

import java.util.UUID;

public class API {
    private static final StorageExecutor EXECUTOR = HostManager.getExecutor();

    /**
     * Get tickets amount of player
     * @param uuid uuid of player
     * @return ticket amount
     */
    public static Integer getTickets(UUID uuid) throws StorageExecuteException {
        return EXECUTOR.getTicket(uuid);
    }

    /**
     * Get tickets amount of player
     * @param player player
     * @return ticket amount
     */
    public static Integer getTickets(Player player) throws StorageExecuteException {
        return getTickets(player.getUniqueId());
    }

    /**
     * Get tickets amount of player
     * @param player player
     */
    public static void addPlayerTickets(Player player, Integer amount) throws StorageExecuteException {
        EXECUTOR.addPlayerTickets(player.getUniqueId(), player.getName(), amount);
    }
}
