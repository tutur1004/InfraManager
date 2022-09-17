package fr.milekat.infra.manager.common.messaging.sending;

import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.messaging.MessageCase;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingSendException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;

public class MessageToHost {
    /**
     * Notify host: Requested player not found in lobby or processing error !
     */
    public static void notifyNotFound(@NotNull UUID invitedPlayer, String host) throws MessagingSendException {
        Main.getMessaging().sendMessage(host, MessageCase.INVITE_RESULT_NOT_FOUND,
                Collections.singletonList(invitedPlayer.toString()));
    }

    /**
     * Notify host: Invitation to player sent !
     */
    public static void notifyInviteSent(@NotNull UUID invitedPlayer, String host) throws MessagingSendException {
        Main.getMessaging().sendMessage(host, MessageCase.INVITE_SENT,
                Collections.singletonList(invitedPlayer.toString()));
    }
}
