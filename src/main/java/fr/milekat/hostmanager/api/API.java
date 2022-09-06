package fr.milekat.hostmanager.api;

import fr.milekat.hostmanager.api.classes.User;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.storage.StorageExecutor;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public class API {
    private static final StorageExecutor EXECUTOR = Main.getStorage();

    /**
     * Get tickets amount of player
     * @param uuid {@link UUID}
     * @return ticket amount
     */
    public static Integer getTickets(UUID uuid) throws StorageExecuteException {
        return EXECUTOR.getTicket(uuid);
    }

    /**
     * Add tickets to player
     * @param uuid {@link UUID}
     */
    public static void addPlayerTickets(UUID uuid, Integer amount) throws StorageExecuteException {
        EXECUTOR.addPlayerTickets(uuid, amount);
    }

    /**
     * Add tickets to player
     * @param uuid {@link UUID}
     */
    public static void removePlayerTickets(UUID uuid, Integer amount) throws StorageExecuteException {
        EXECUTOR.removePlayerTickets(uuid, amount);
    }

    /**
     * Get a user if present, otherwise return null
     * @param uuid of {@link UUID}
     * @return User or null
     */
    @Nullable
    public static User getUser(UUID uuid) throws StorageExecuteException {
        return EXECUTOR.getUser(uuid);
    }
}
