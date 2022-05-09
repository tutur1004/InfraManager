package fr.milekat.hostmanager.api;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.storage.StorageExecutor;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class API {
    private static final StorageExecutor EXECUTOR = Main.getExecutor();

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
    public static Integer getTickets(ProxiedPlayer player) throws StorageExecuteException {
        return getTickets(player.getUniqueId());
    }

    /**
     * Add tickets to player
     * @param player player
     */
    public static void addPlayerTickets(ProxiedPlayer player, Integer amount) throws StorageExecuteException {
        EXECUTOR.addPlayerTickets(player.getUniqueId(), player.getName(), amount);
    }
}
