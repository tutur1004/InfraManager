package fr.milekat.infra.manager.common.messaging.processing;

import fr.milekat.infra.manager.api.classes.Game;
import fr.milekat.infra.manager.api.classes.User;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.messaging.MessageCase;
import fr.milekat.infra.manager.common.messaging.Messaging;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.manager.common.messaging.sending.MessageToLobby;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageFromLobby {
    public MessageFromLobby(@NotNull List<String> message) {
        String source = message.get(0);
        String server = source.replaceAll(Messaging.PREFIX, "")
                .replaceAll("\\.", "-");
        MessageCase mCase = MessageCase.valueOf(message.get(1));
        switch (mCase) {
            case ASK_CREATE_HOST: {
                if (message.size() == 4) {
                    UUID uuid = null;
                    try {
                        uuid = UUID.fromString(message.get(2));
                        Game game = Main.getStorage().getGame(message.get(3));
                        User user = Main.getStorage().getUser(uuid);
                        Main.getHosts().createHost(game, user);
                    } catch (IllegalArgumentException | StorageExecuteException | HostExecuteException exception) {
                        if (Main.DEBUG) {
                            Main.getLogger().warn(mCase.name() + " ignored, message: " + message);
                        }
                        try {
                            MessageToLobby.notifyCreateHostFailed(Objects.requireNonNull(uuid), source);
                        } catch (NullPointerException | MessagingSendException sendException) {
                            Main.getLogger().trace("Can't notify player after getting an error to create a host");
                            if (Main.DEBUG) {
                                sendException.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (Main.DEBUG) {
                        Main.getLogger().warn(mCase.name() + " ignored, message: " + message);
                    }
                }
                break;
            }
            default: {
                if (Main.DEBUG) {
                    Main.getLogger().warn("Receive message from lobby with unknown case, message: " + message);
                }
            }
        }
    }
}
