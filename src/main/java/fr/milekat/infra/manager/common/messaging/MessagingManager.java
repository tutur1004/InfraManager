package fr.milekat.infra.manager.common.messaging;

import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.messaging.adapter.rabbitmq.ReceiveRabbitMessage;
import fr.milekat.infra.manager.common.messaging.adapter.rabbitmq.SendRabbitMessage;
import fr.milekat.infra.manager.common.messaging.adapter.redis.SendRedisMessage;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingLoaderException;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.manager.common.utils.Configs;
import org.jetbrains.annotations.NotNull;

public class MessagingManager {
    private final Messaging messaging;

    public MessagingManager(@NotNull Configs config) throws MessagingLoaderException {
        try {
            String messagingProvider = config.getString("messaging.type");
            if (Main.DEBUG) {
                Main.getLogger().info("Loading messaging type: " + messagingProvider);
            }
            if (messagingProvider.equalsIgnoreCase("rabbitmq")) {
                new ReceiveRabbitMessage().getRabbitConsumerThread().start();
                messaging = new SendRabbitMessage();
            } else if (messagingProvider.equalsIgnoreCase("redis")) {
                messaging = new SendRedisMessage();
            } else {
                throw new MessagingLoaderException("Unsupported messaging type");
            }
            if (messaging.checkSending()) {
                if (Main.DEBUG) {
                    Main.getLogger().info("Messaging loaded");
                }
            } else {
                throw new MessagingLoaderException("Messaging are not loaded properly");
            }
        } catch (MessagingSendException exception) {
            throw new MessagingLoaderException("Can't load messaging properly");
        }
    }

    public Messaging getMessaging() {
        return this.messaging;
    }
}
