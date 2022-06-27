package fr.milekat.hostmanager.hosts;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.hosts.bungee.ServerManager;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;

public interface HostExecutor {
    /**
     * Create a new server instance in provider
     */
    void createServer(Instance instance) throws HostExecuteException, StorageExecuteException;

    /**
     * Delete an existing server instance from provider
     */
    void deleteServer(Instance instance) throws HostExecuteException;

    /**
     * Check if host provider is reachable
     */
    boolean checkAvailability() throws HostExecuteException;

    /**
     * Load all hosts in bungee-cord server list
     */
    default void resetBungeeHostList() throws StorageExecuteException {
        ServerManager.removeServersPrefix(Main.HOST_BUNGEE_SERVER_PREFIX);
        for (Instance server : Main.getStorage().getActiveInstances()) {
            ServerManager.addServer(Main.HOST_BUNGEE_SERVER_PREFIX + server.getName(), server.getPort());
        }
    }
}
