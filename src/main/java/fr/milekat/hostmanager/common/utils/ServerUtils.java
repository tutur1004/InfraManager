package fr.milekat.hostmanager.common.utils;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;

public interface ServerUtils {
    void reconnectAllPlayersToLobby(Instance instance);

    void resetHostList() throws StorageExecuteException;

    void addServer(String name, int port);

    void removeServer(String name);

    void removeServersPrefix(String prefix);
}
