package fr.milekat.infra.manager.common.messaging.adapter.minecraft;

import fr.milekat.infra.manager.common.messaging.Messaging;
import fr.milekat.infra.manager.common.messaging.MessagingCase;

import java.util.List;
import java.util.UUID;

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
     * Send a message to the proxy server
     *
     * @param p       source player
     * @param target  Targeted channel (MainChannel for PluginMessage)
     * @param mCase   Type of message
     * @param message to send
     */
    @Override
    public void sendMessage(UUID p, String target, MessagingCase mCase, List<String> message) {
        // TODO: 14/09/2022 todo :)
    }
}
