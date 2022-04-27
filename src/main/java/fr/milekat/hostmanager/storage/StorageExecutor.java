package fr.milekat.hostmanager.storage;

import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;

import java.util.UUID;

public interface StorageExecutor {
    /**
     * Disconnect from Storage server
     */
    void disconnect();

    /**
     * Check if all storages are loaded
     * @return true if all storages are loaded
     */
    boolean checkStorages();

    /**
     * Query tickets for this player
     * @param uuid player uuid
     * @return amount of reaming ticket
     */
    Integer getTicket(UUID uuid) throws StorageExecuteException;

    /**
     * Add tickets to this player
     * @param uuid player uuid
     * @param username player minecraft username
     * @param amount amount of tickets to add to this player
     */
    void addPlayerTickets(UUID uuid, String username, Integer amount) throws StorageExecuteException;
}
