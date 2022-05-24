package fr.milekat.hostmanager.api.events;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.api.classes.User;
import net.md_5.bungee.api.plugin.Event;

public class ServerDeletedEvent extends Event {
    private final Instance instance;
    private final User user;

    public ServerDeletedEvent(Instance instance) {
        this.instance = instance;
        this.user = null;
    }

    public ServerDeletedEvent(Instance instance, User user) {
        this.instance = instance;
        this.user = user;
    }

    public Instance getInstance() {
        return instance;
    }
}
