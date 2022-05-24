package fr.milekat.hostmanager.hosts;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;

public interface HostExecutor {
    /**
     * Create a new server instance in provider
     */
    void createServer(Instance instance) throws HostExecuteException;

    /**
     * Delete an existing server instance from provider
     */
    void deleteServer(String serverName) throws HostExecuteException;

    /**
     * Check if host provider is reachable
     */
    boolean checkAvailability() throws HostExecuteException;
}
