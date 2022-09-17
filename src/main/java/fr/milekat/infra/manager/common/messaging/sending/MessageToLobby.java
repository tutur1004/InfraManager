package fr.milekat.infra.manager.common.messaging.sending;

import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.messaging.MessageCase;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingSendException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;

public class MessageToLobby {
    /**
     * Notify lobby: Host request has failed !
     */
    public static void notifyCreateHostFailed(@NotNull UUID hostPlayer, String lobby) throws MessagingSendException {
        Main.getMessaging().sendMessage(lobby, MessageCase.CREATE_HOST_FAILED,
                Collections.singletonList(hostPlayer.toString()));
    }
}
