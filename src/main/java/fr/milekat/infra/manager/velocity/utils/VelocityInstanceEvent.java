package fr.milekat.infra.manager.velocity.utils;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.utils.CommonEvent;
import fr.milekat.infra.manager.common.utils.EventCaller;
import fr.milekat.infra.manager.velocity.events.ServerCreatedEvent;
import fr.milekat.infra.manager.velocity.events.ServerDeletedEvent;
import fr.milekat.infra.manager.velocity.events.ServerDeletionEvent;

public class VelocityInstanceEvent implements EventCaller {
    private final ProxyServer server;

    public VelocityInstanceEvent(ProxyServer server) {
        this.server = server;
    }

    /**
     * Create a new InstanceEvent for Velocity events
     */
    @Override
    public InstanceEvent callEvent(CommonEvent.EventName eventName, Instance instance) {
        return new InstanceEvent(server, eventName, instance);
    }

    static class InstanceEvent implements CommonEvent {
        private ResultedEvent<ResultedEvent.GenericResult> event;

        public InstanceEvent(ProxyServer server, EventName eventName, Instance instance) {
            switch (eventName) {
                case ServerCreatedEvent: {
                    event = new ServerCreatedEvent(instance);
                    server.getEventManager().fire(event);
                    break;
                }
                case ServerDeletedEvent: {
                    event = new ServerDeletedEvent(instance);
                    server.getEventManager().fire(event);
                    break;
                }
                case ServerDeletionEvent: {
                    event = new ServerDeletionEvent(instance);
                    server.getEventManager().fire(event);
                    break;
                }
            }
        }

        /**
         * Set the event result
         */
        @Override
        public void setCancel(boolean cancel) {
            event.setResult(cancel ?
                    ResultedEvent.GenericResult.denied() :
                    ResultedEvent.GenericResult.allowed());
        }

        /**
         * Check if event is cancelled
         */
        @Override
        public boolean isCancelled() {
            return !event.getResult().isAllowed();
        }
    }
}
