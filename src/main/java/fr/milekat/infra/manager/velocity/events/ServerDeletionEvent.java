package fr.milekat.infra.manager.velocity.events;

import com.velocitypowered.api.event.ResultedEvent;
import fr.milekat.infra.manager.api.classes.Instance;

import java.util.Objects;

public class ServerDeletionEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    private final Instance instance;

    private GenericResult result = GenericResult.allowed();

    public ServerDeletionEvent(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }

    @Override
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = Objects.requireNonNull(result);
    }
}
