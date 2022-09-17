package fr.milekat.infra.manager.common.messaging.processing;

import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.api.classes.User;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.messaging.MessageCase;
import fr.milekat.infra.manager.common.messaging.Messaging;
import fr.milekat.infra.manager.common.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.manager.common.messaging.sending.MessageToHost;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageFromHost {
    public MessageFromHost(@NotNull List<String> message) {
        String source = message.get(0);
        String server = source.replaceAll(Messaging.PREFIX, "")
                .replaceAll("\\.", "-");
        MessageCase mCase = MessageCase.valueOf(message.get(1));
        switch (mCase) {
            case GAME_READY: {
                try {
                    Instance instance = Main.getStorage().getInstance(server);
                    User user = Objects.requireNonNull(instance).getUser();
                    if (Main.getUtils().getInfraUtils().playerIsInLobby(user.getUuid())) {
                        Main.getUtils().getInfraUtils().sendPlayerToServer(user.getUuid(), server);
                    }
                } catch (IllegalArgumentException | StorageExecuteException | NullPointerException exception) {
                    if (Main.DEBUG) {
                        Main.getLogger().trace("Error while receiving a " + mCase.name() + " status");
                        exception.printStackTrace();
                    }
                }
                break;
            }
            case GAME_FINISHED: {
                try {
                    Instance instance = Main.getStorage().getInstance(server);
                    Main.getHosts().deleteHost(instance);
                } catch (HostExecuteException | StorageExecuteException | NullPointerException exception) {
                    if (Main.DEBUG) {
                        Main.getLogger().trace("Error while receiving a " + mCase.name() + " status");
                        exception.printStackTrace();
                    }
                }
                break;
            }
            case HOST_JOINED: {
                if (Main.DEBUG) {
                    Main.getLogger().info(mCase.name() + ".. Well.. It's cool..");
                }
                break;
            }
            case HOST_INVITE_PLAYER: {
                if (message.size() == 3) {
                    UUID target = null;
                    try {
                        Instance instance = Main.getStorage().getInstance(server);
                        User host = Objects.requireNonNull(instance).getUser();
                        target = UUID.fromString(message.get(2));
                        if (Main.getUtils().getInfraUtils().playerIsInLobby(target)) {
                            Main.getUtils().getInfraUtils().sendPlayerMessage(target, host.getLastName() +
                                    " ask you to join instance : " + instance.getName());
                            MessageToHost.notifyInviteSent(target, source);
                            // TODO: 17/09/2022 Send invitation with Yes/No ?
                        } else {
                            MessageToHost.notifyNotFound(target, source);
                        }
                    } catch (IllegalArgumentException | StorageExecuteException | MessagingSendException exception) {
                        if (Main.DEBUG) {
                            exception.printStackTrace();
                        }
                        try {
                            MessageToHost.notifyNotFound(Objects.requireNonNull(target), source);
                        } catch (NullPointerException | MessagingSendException sendException) {
                            Main.getLogger().warn("Can't notify " + MessageCase.INVITE_RESULT_NOT_FOUND.name());
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
            case HOST_DENIED_REQUEST: {
                if (message.size() == 3) {
                    UUID target = null;
                    try {
                        Instance instance = Main.getStorage().getInstance(server);
                        User host = Objects.requireNonNull(instance).getUser();
                        target = UUID.fromString(message.get(2));
                        if (Main.getUtils().getInfraUtils().playerIsInLobby(target)) {
                            Main.getUtils().getInfraUtils().sendPlayerMessage(target, host.getLastName() +
                                    " won't you finally :D");
                        }
                    } catch (Exception sendException) {
                        Main.getLogger().trace("Can't notify player after getting an error to create a host");
                        if (Main.DEBUG) {
                            sendException.printStackTrace();
                        }
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