package fr.milekat.hostmanager.velocity.utils;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.utils.CommonEvent;
import fr.milekat.hostmanager.common.utils.EventCaller;
import fr.milekat.hostmanager.velocity.events.ServerCreatedEvent;
import fr.milekat.hostmanager.velocity.events.ServerDeletedEvent;
import fr.milekat.hostmanager.velocity.events.ServerDeletionEvent;

public class VelocityInstanceEvent implements EventCaller {
    private final ProxyServer server;

    public VelocityInstanceEvent(ProxyServer server) {
        this.server = server;
    }

    public InstanceEvent callEvent(CommonEvent.EventName eventName, Instance instance) {
        return new InstanceEvent(server, eventName, instance);
    }

    static class InstanceEvent implements CommonEvent {
        private final EventName eventName;

        private ServerCreatedEvent serverCreatedEvent;
        private ServerDeletionEvent serverDeletionEvent;

        public InstanceEvent(ProxyServer server, EventName eventName, Instance instance) {
            this.eventName = eventName;
            switch (eventName) {
                case ServerCreatedEvent: {
                    serverCreatedEvent = new ServerCreatedEvent(instance);
                    server.getEventManager().fire(serverCreatedEvent);
                    break;
                }
                case ServerDeletedEvent: {
                    ServerDeletedEvent serverDeletedEvent = new ServerDeletedEvent(instance);
                    server.getEventManager().fire(serverDeletedEvent);
                    break;
                }
                case ServerDeletionEvent: {
                    serverDeletionEvent = new ServerDeletionEvent(instance);
                    server.getEventManager().fire(serverDeletionEvent);
                    break;
                }
            }
        }

        @Override
        public void setCancel(boolean cancel) {
            switch (eventName) {
                case ServerCreatedEvent: {
                    serverCreatedEvent.setResult(cancel ?
                            ResultedEvent.GenericResult.denied() :
                            ResultedEvent.GenericResult.allowed());
                    break;
                }
                case ServerDeletionEvent: {
                    serverDeletionEvent.setResult(cancel ?
                            ResultedEvent.GenericResult.denied() :
                            ResultedEvent.GenericResult.allowed());
                    break;
                }
            }
        }

        @Override
        public boolean isCancelled() {
            switch (eventName) {
                case ServerCreatedEvent: {
                    return !serverCreatedEvent.getResult().isAllowed();
                }
                case ServerDeletionEvent: {
                    return !serverDeletionEvent.getResult().isAllowed();
                }
            }
            return false;
        }
    }
}
