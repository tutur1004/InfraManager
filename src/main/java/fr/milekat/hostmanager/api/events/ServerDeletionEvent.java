package fr.milekat.hostmanager.api.events;

import fr.milekat.hostmanager.api.classes.Instance;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ServerDeletionEvent extends Event implements Cancellable {
    private final Instance instance;
    private boolean canceled;

    public ServerDeletionEvent(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }
}
