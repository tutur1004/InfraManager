package fr.milekat.hostmanager.bungeecord.utils;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.bungeecord.events.ServerCreatedEvent;
import fr.milekat.hostmanager.bungeecord.events.ServerDeletedEvent;
import fr.milekat.hostmanager.bungeecord.events.ServerDeletionEvent;
import fr.milekat.hostmanager.common.utils.CommonEvent;
import fr.milekat.hostmanager.common.utils.EventCaller;
import net.md_5.bungee.api.ProxyServer;

public class BungeeInstanceEvent implements EventCaller {
    /**
     * Create a new InstanceEvent for Bungee events
     */
    @Override
    public InstanceEvent callEvent(CommonEvent.EventName eventName, Instance instance) {
        return new InstanceEvent(eventName, instance);
    }

    static class InstanceEvent implements CommonEvent {
        private final EventName eventName;

        private ServerCreatedEvent serverCreatedEvent;
        private ServerDeletionEvent serverDeletionEvent;

        public InstanceEvent(CommonEvent.EventName eventName, Instance instance) {
            this.eventName = eventName;
            switch (eventName) {
                case ServerCreatedEvent: {
                    serverCreatedEvent = new ServerCreatedEvent(instance);
                    ProxyServer.getInstance().getPluginManager().callEvent(serverCreatedEvent);
                    break;
                }
                case ServerDeletedEvent: {
                    ServerDeletedEvent serverDeletedEvent = new ServerDeletedEvent(instance);
                    ProxyServer.getInstance().getPluginManager().callEvent(serverDeletedEvent);
                    break;
                }
                case ServerDeletionEvent: {
                    serverDeletionEvent = new ServerDeletionEvent(instance);
                    ProxyServer.getInstance().getPluginManager().callEvent(serverDeletionEvent);
                    break;
                }
            }
        }

        /**
         * Cancel the event
         */
        @Override
        public void setCancel(boolean cancel) {
            switch (eventName) {
                case ServerCreatedEvent: {
                    serverCreatedEvent.setCancelled(cancel);
                    break;
                }
                case ServerDeletionEvent: {
                    serverDeletionEvent.setCancelled(cancel);
                    break;
                }
            }
        }

        /**
         * Check if event is cancelled
         */
        @Override
        public boolean isCancelled() {
            switch (eventName) {
                case ServerCreatedEvent: {
                    return serverCreatedEvent.isCancelled();
                }
                case ServerDeletionEvent: {
                    return serverDeletionEvent.isCancelled();
                }
            }
            return false;
        }
    }
}
