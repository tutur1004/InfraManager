package fr.milekat.hostmanager.api;

import fr.milekat.hostmanager.HostManager;
import fr.milekat.hostmanager.storage.StorageExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class API {
    private static final StorageExecutor EXECUTOR = HostManager.getExecutor();

    /**
     * Get tickets amount of player
     * @param uuid uuid of player
     * @return ticket amount
     */
    public static Integer getTickets(UUID uuid) throws SQLException {
        return EXECUTOR.getTicket(uuid);
    }

    /**
     * Get tickets amount of player
     * @param player player
     * @return ticket amount
     */
    public static Integer getTickets(Player player) throws SQLException {
        return getTickets(player.getUniqueId());
    }

    /**
     * Get tickets amount of player
     * @param player player
     */
    public static void addPlayerTickets(Player player, Integer amount) throws SQLException {
        EXECUTOR.addPlayerTickets(player.getUniqueId(), player.getName(), amount);
    }
}
