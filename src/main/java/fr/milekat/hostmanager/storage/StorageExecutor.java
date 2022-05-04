package fr.milekat.hostmanager.storage;

import fr.milekat.hostmanager.hosts.classes.Game;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;

import java.util.ArrayList;
import java.util.List;
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

    /*
        Tickets
     */

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

    /**
     * Remove tickets to this player
     * @param uuid player uuid
     * @param username player minecraft username
     * @param amount amount of tickets to remove to this player
     */
    void removePlayerTickets(UUID uuid, String username, Integer amount) throws StorageExecuteException;

    /*
        Games
     */
    /**
     * Get all games
     * @return list of games
     */
    List<Game> getGames() throws StorageExecuteException;

    /**
     * Create a new game
     */
    void createGame(Game game) throws StorageExecuteException;

    /**
     * Update an existing game
     */
    void updateGame(Game game) throws StorageExecuteException;

    /*
        Instances
     */
    // TODO: 01/05/2022 Instances queries
}
