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
                        Game game = Main.getStorage().getGameCached(Integer.parseInt(message.get(3)));
                        if (game==null) {
                            if (Main.DEBUG) {
                                Main.getLogger().warn("Game not found");
                            }
                            return;
                        }
                        User user = Main.getStorage().getUser(uuid);
                        if (user==null) {
                            if (Main.DEBUG) {
                                Main.getLogger().warn("User not found");
                            }
                            return;
                        }
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
            case SEND_PLAYER: {
                if (message.size() == 4) {
                    UUID uuid = null;
                    try {
                        uuid = UUID.fromString(message.get(2));
                        Main.getUtils().getInfraUtils().sendPlayerToServer(uuid, message.get(3));
                    } catch (IllegalArgumentException exception) {
                        if (Main.DEBUG) {
                            Main.getLogger().warn(mCase.name() + " ignored, message: " + message);
                        }
                        try {
                            // TODO: 26/09/2022 notify can't teleport
                        } catch (NullPointerException sendException) {
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
