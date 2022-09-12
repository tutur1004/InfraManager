package fr.milekat.infra.manager.common.utils;

import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;

public interface HostUtils {
    void reconnectAllPlayersToLobby(Instance instance);

    void resetHostList() throws StorageExecuteException;

    void addServer(String name, String hostname, int port);

    void removeServer(String name);

    void removeServersPrefix(String prefix);
}
