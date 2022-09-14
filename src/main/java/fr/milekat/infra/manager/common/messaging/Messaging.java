package fr.milekat.infra.manager.common.messaging;

import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingSendException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface Messaging {
    //  Global settings
    String PROXY_PREFIX = "proxy";
    String LOBBY_PREFIX = "lobby";
    String HOST_PREFIX = "host";

    //  RabbitMQ settings
    String RABBIT_SEPARATOR = ".";
    String RABBIT_PREFIX = Main.getConfig().getString("messaging.rabbit-mq.prefix");
    String RABBIT_EXCHANGE_TYPE = "x-rtopic";
    String RABBIT_EXCHANGE = RABBIT_PREFIX + RABBIT_EXCHANGE_TYPE + RABBIT_SEPARATOR + "exchange";
    String RABBIT_QUEUE = RABBIT_PREFIX + "queue" + RABBIT_SEPARATOR + getServerIdentifier();
    String RABBIT_ROUTING_KEY = RABBIT_PREFIX + getServerIdentifier();

    /**
     * Simple shortcut to get the server identifier
     */
    static @NotNull String getServerIdentifier() {
        return PROXY_PREFIX;
    }

    /**
     * Check if the provider is reachable
     * @return true if message sent successfully
     * @throws MessagingSendException triggered if send failed
     */
    boolean checkSending() throws MessagingSendException;

    /**
     * Send a message to the proxy server
     *
     * @param player  source player
     * @param target  Targeted channel (MainChannel for PluginMessage, RoutingKey for RabbitMQ)
     * @param mCase   Type of message
     * @param message to send
     */
    void sendMessage(UUID player, String target, MessagingCase mCase, List<String> message)
            throws MessagingSendException;

    /**
     * Disconnect from the message provider
     */
    void disconnect();
}
