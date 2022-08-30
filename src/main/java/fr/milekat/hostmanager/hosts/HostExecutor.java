package fr.milekat.hostmanager.hosts;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;

public interface HostExecutor {
    /**
     * Check if host provider is reachable
     */
    boolean checkAvailability() throws HostExecuteException;

    /**
     * Create a new server instance in provider
     */
    void createServer(Instance instance) throws HostExecuteException, StorageExecuteException;

    /**
     * Delete an existing server instance from provider
     */
    void deleteServer(Instance instance) throws HostExecuteException;
}
