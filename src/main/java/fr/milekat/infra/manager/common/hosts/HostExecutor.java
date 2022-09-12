package fr.milekat.infra.manager.common.hosts;

import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
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
