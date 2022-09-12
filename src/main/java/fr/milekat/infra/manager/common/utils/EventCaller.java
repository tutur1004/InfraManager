package fr.milekat.infra.manager.common.utils;

import fr.milekat.infra.manager.api.classes.Instance;

public interface EventCaller {
    CommonEvent callEvent(CommonEvent.EventName eventName, Instance instance);
}
