package fr.milekat.hostmanager.api.events;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.api.classes.User;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ServerDeletionEvent extends Event implements Cancellable {
    private final Instance instance;
    private final User user;
    private boolean canceled;

    public ServerDeletionEvent(Instance instance) {
        this.instance = instance;
        this.user = null;
    }

    public ServerDeletionEvent(Instance instance, User user) {
        this.instance = instance;
        this.user = user;
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
