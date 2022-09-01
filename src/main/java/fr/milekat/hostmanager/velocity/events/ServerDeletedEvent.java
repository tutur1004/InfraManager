package fr.milekat.hostmanager.velocity.events;

import fr.milekat.hostmanager.api.classes.Instance;

public class ServerDeletedEvent {
    private final Instance instance;

    public ServerDeletedEvent(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }
}
