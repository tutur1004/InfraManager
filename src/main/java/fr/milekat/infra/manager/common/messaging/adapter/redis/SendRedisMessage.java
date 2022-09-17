package fr.milekat.infra.manager.common.messaging.adapter.redis;

import fr.milekat.infra.manager.common.messaging.MessageCase;
import fr.milekat.infra.manager.common.messaging.Messaging;

import java.util.List;

public class SendRedisMessage implements Messaging {
    @Override
    public boolean checkSending() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    /**
     * Send a message via RedisPubSub
     *
     * @param target  Targeted channel
     * @param mCase   Type of message
     * @param message to send
     */
    @Override
    public void sendMessage(String target, MessageCase mCase, List<String> message) {

    }
}
