package fr.milekat.hostmanager.common.storage;

import fr.milekat.hostmanager.api.classes.Game;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.api.classes.Log;
import fr.milekat.hostmanager.api.classes.User;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface StorageExecutor {
    /**
     * Disconnect from Storage server
     */
    void disconnect();

    /**
     * Check if all storages are loaded
     * @return true if all storages are loaded
     */
    boolean checkStorages() throws StorageExecuteException;

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
     * Get last queried list of games (If list is too old, or not exist, it will re-queried the list)
     * @return "recent" list of games
     */
    List<Game> getGamesCached() throws StorageExecuteException;

    /**
     * Get a games by name
     * @return game or null if not exist
     */
    Game getGame(String name) throws StorageExecuteException;

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

    /**
     * Query active instances
     * @return list of instances
     */
    List<Instance> getActiveInstances() throws StorageExecuteException;

    /**
     * Query an instance
     * @param name of instance
     * @return instance (If exist)
     */
    @Nullable
    Instance getInstance(String name) throws StorageExecuteException;

    /**
     * Create a new instance
     */
    void createInstance(Instance instance) throws StorageExecuteException;

    /**
     * Save an instance
     */
    void updateInstance(Instance instance) throws StorageExecuteException;

    /**
     * Find an available port in given list
     * @return a port number or null if all ports are reserved
     */
    @Nullable
    Integer findAvailablePort(List<Integer> ports) throws StorageExecuteException;

    /*
        Users
     */

    /**
     * Query all users ! WARNING THIS CAN BE A HUGE STORAGE QUERY
     * @return all users
     */
    List<User> getUsers() throws StorageExecuteException;

    /**
     * Query a user by his name if present, otherwise return null
     * @param name of player
     * @return User or null
     */
    @Nullable
    User getUser(String name) throws StorageExecuteException;

    /**
     * Query a user if present, otherwise return null
     * @param uuid of player
     * @return User or null
     */
    @Nullable
    User getUser(UUID uuid) throws StorageExecuteException;

    /*
        Logs
     */

    /**
     * Retrieve last n logs
     * @param count number of the latest logs to retrieve
     */
    List<Log> getLogs(int count) throws StorageExecuteException;

    /**
     * Retrieve all logs between 2 days ! WARNING THIS CAN BE A HUGE STORAGE QUERY
     * @param from first date from the period
     * @param to end of the period
     */
    List<Log> getLogs(Date from, Date to) throws StorageExecuteException;
}
