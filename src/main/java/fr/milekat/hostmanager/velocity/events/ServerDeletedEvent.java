package fr.milekat.hostmanager.velocity.events;

import com.velocitypowered.api.event.ResultedEvent;
import fr.milekat.hostmanager.api.classes.Instance;

public class ServerDeletedEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    private final Instance instance;

    public ServerDeletedEvent(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }

    @Override
    public GenericResult getResult() {
        return GenericResult.allowed();
    }

    @Override
    public void setResult(GenericResult ignored) {}
}
