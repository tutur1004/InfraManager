package fr.milekat.infra.manager.common.messaging.adapter.rabbitmq;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.messaging.MessageCase;
import fr.milekat.infra.manager.common.messaging.Messaging;
import fr.milekat.infra.manager.common.messaging.processing.MessageFromHost;
import fr.milekat.infra.manager.common.messaging.processing.MessageFromLobby;
import fr.milekat.infra.manager.common.messaging.processing.MessageFromProxy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ReceiveRabbitMessage {
    private final ConnectionFactory factory;

    public ReceiveRabbitMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Main.getConfig().getString("messaging.rabbit-mq.hostname"));
        connectionFactory.setPort(Main.getConfig().getInt("messaging.rabbit-mq.port"));
        connectionFactory.setUsername(Main.getConfig().getString("messaging.rabbit-mq.username"));
        connectionFactory.setPassword(Main.getConfig().getString("messaging.rabbit-mq.password"));
        this.factory = connectionFactory;
    }

    /**
     * Get a new RabbitMQ Consumer Thread
     */
    public Thread getRabbitConsumerThread() {
        if (Main.DEBUG) Main.getLogger().info("Loading RabbitMQ consumer..");
        return new Thread(() -> {
            try {
                Connection connection = this.factory.newConnection();
                Channel channel = connection.createChannel();
                channel.exchangeDeclare(Messaging.RABBIT_EXCHANGE, Messaging.RABBIT_EXCHANGE_TYPE);
                channel.queueDeclare(Messaging.RABBIT_QUEUE, false, true,
                        true, null);
                channel.queueBind(Messaging.RABBIT_QUEUE, Messaging.RABBIT_EXCHANGE,
                        Messaging.RABBIT_ROUTING_KEY);
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String strRaw = "";
                    try {
                        strRaw = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        if (Main.DEBUG) {
                            Main.getLogger().info(strRaw);
                        }
                        List<String> message = new Gson().fromJson(strRaw, new TypeToken<List<String>>(){}.getType());
                        if (message.size() < 2) {
                            if (Main.DEBUG) {
                                Main.getLogger().warn(MessageCase.class.getName()+" not found in message: "+message);
                            }
                            return;
                        }
                        if (message.get(0).startsWith(Messaging.PREFIX + Main.PROXY_PREFIX)) {
                            //  Message is sent from a proxy server
                            new MessageFromProxy(message);
                        } else if (message.get(0).startsWith(Messaging.PREFIX + Main.LOBBY_PREFIX)) {
                            //  Message is sent from a lobby server
                            new MessageFromLobby(message);
                        } else if (message.get(0).startsWith(Messaging.PREFIX + Main.HOST_PREFIX)) {
                            //  Message is sent from a host server
                            new MessageFromHost(message);
                        }
                    } catch (Exception exception) {
                        if (Main.DEBUG) {
                            Main.getLogger().warn("Error while trying to consume Rabbit message !");
                            Main.getLogger().warn("Message: [" + strRaw + "]");
                            exception.printStackTrace();
                        }
                    }
                };
                channel.basicConsume(Messaging.RABBIT_QUEUE, true, deliverCallback, consumerTag -> {});
            } catch (IOException | TimeoutException exception) {
                exception.printStackTrace();
            }
        });
    }
}
