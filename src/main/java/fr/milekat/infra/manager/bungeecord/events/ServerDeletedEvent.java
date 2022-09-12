package fr.milekat.infra.manager.bungeecord.events;

import fr.milekat.infra.manager.api.classes.Instance;
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
