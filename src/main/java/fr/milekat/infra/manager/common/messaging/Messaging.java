package fr.milekat.infra.manager.common.messaging;

import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingSendException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <p>Messages semantic:</p>
 * <p>0. {@link Messaging#getServerIdentifier()}
 * <br>1. {@link MessageCase}
 * <br>2.[...] Message arguments</p>
 * */
public interface Messaging {
    //  Global settings
    String SEPARATOR = ".";
    String PREFIX = Main.getConfig().getString("messaging.prefix");

    //  RabbitMQ settings
    String RABBIT_EXCHANGE_TYPE = "x-rtopic";
    String RABBIT_EXCHANGE = PREFIX + RABBIT_EXCHANGE_TYPE + SEPARATOR + "exchange";
    String RABBIT_QUEUE = PREFIX + "queue" + SEPARATOR + getServerIdentifier();
    String RABBIT_ROUTING_KEY = PREFIX + getServerIdentifier();

    /**
     * Simple shortcut to get the server identifier
     */
    static @NotNull String getServerIdentifier() {
        return Main.PROXY_PREFIX + SEPARATOR + Main.PORT;
    }

    /**
     * Check if the provider is reachable
     * @return true if message sent successfully
     * @throws MessagingSendException triggered if send failed
     */
    boolean checkSending() throws MessagingSendException;

    /**
     * Send a message through messaging provider
     *
     * @param target  Targeted channel
     * @param mCase   Type of message
     * @param message to send
     */
    void sendMessage(String target, MessageCase mCase, List<String> message)
            throws MessagingSendException;

    /**
     * Disconnect from the message provider
     */
    void disconnect();
}
