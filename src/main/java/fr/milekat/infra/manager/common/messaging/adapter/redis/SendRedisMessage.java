package fr.milekat.infra.manager.common.messaging.adapter.redis;

import fr.milekat.infra.manager.common.messaging.Messaging;
import fr.milekat.infra.manager.common.messaging.MessagingCase;

import java.util.List;
import java.util.UUID;

public class SendRedisMessage implements Messaging {
    @Override
    public boolean checkSending() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    /**
     * Send a message to the proxy server
     *
     * @param target
     * @param message to send
     */
    @Override
    public void sendMessage(UUID player, String target, MessagingCase mCase, List<String> message) {

    }
}
