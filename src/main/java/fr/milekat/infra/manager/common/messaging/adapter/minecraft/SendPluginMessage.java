package fr.milekat.infra.manager.common.messaging.adapter.minecraft;

import fr.milekat.infra.manager.common.messaging.MessageCase;
import fr.milekat.infra.manager.common.messaging.Messaging;

import java.util.List;

public class SendPluginMessage implements Messaging {
    @Override
    public boolean checkSending() {
        return false;
    }

    @Override
    public void disconnect() {
        // TODO: 14/09/2022 todo :)
    }

    /**
     * Send a message
     *
     * @param target  Targeted channel
     * @param mCase   Type of message
     * @param message to send
     */
    @Override
    public void sendMessage(String target, MessageCase mCase, List<String> message) {
        // TODO: 14/09/2022 todo :)
    }
}
