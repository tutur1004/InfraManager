package fr.milekat.hostmanager.common.hosts;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;
import org.jetbrains.annotations.NotNull;

public interface HostExecutor {
    /**
     * Check if host provider is reachable
     */
    boolean checkAvailability() throws HostExecuteException;

    /**
     * Create a new server instance in provider
     */
    void createServer(@NotNull Instance instance) throws HostExecuteException, StorageExecuteException;

    /**
     * Delete an existing server instance from provider
     */
    void deleteServer(@NotNull Instance instance) throws HostExecuteException;
}
