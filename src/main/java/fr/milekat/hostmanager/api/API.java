package fr.milekat.hostmanager.api;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.User;
import fr.milekat.hostmanager.storage.StorageExecutor;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public class API {
    private static final StorageExecutor EXECUTOR = Main.getStorage();

    /**
     * Get tickets amount of player
     * @param uuid {@link UUID} of {@link ProxiedPlayer}
     * @return ticket amount
     */
    public static Integer getTickets(UUID uuid) throws StorageExecuteException {
        return EXECUTOR.getTicket(uuid);
    }

    /**
     * Get tickets amount of player
     * @param player {@link ProxiedPlayer}
     * @return ticket amount
     */
    public static Integer getTickets(ProxiedPlayer player) throws StorageExecuteException {
        return getTickets(player.getUniqueId());
    }

    /**
     * Add tickets to player
     * @param player {@link ProxiedPlayer}
     */
    public static void addPlayerTickets(ProxiedPlayer player, Integer amount) throws StorageExecuteException {
        EXECUTOR.addPlayerTickets(player.getUniqueId(), player.getName(), amount);
    }

    /**
     * Get a user if present, otherwise return null
     * @param uuid of {@link ProxiedPlayer}
     * @return User or null
     */
    @Nullable
    public static User getUser(UUID uuid) throws StorageExecuteException {
        return EXECUTOR.getUser(uuid);
    }
}
