package fr.milekat.infra.manager.common.utils;

import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;

import java.util.UUID;

public interface InfraUtils {
    void reconnectAllPlayersToLobby(Instance instance);

    void resetInfraServerList() throws StorageExecuteException;

    void addServer(String name, String hostname, int port);

    void removeServer(String name);

    void removeServersPrefix(String prefix);

    void sendPlayerMessage(UUID uuid, String message);

    void sendPlayerToServer(UUID uuid, String serverName);

    boolean playerIsConnected(UUID uuid);

    boolean playerIsInLobby(UUID uuid);
}
