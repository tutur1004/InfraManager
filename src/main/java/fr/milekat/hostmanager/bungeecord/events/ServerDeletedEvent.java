package fr.milekat.hostmanager.bungeecord.events;

import fr.milekat.hostmanager.api.classes.Instance;
import net.md_5.bungee.api.plugin.Event;

public class ServerDeletedEvent extends Event {
    private final Instance instance;

    public ServerDeletedEvent(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }
}
