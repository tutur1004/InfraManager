package fr.milekat.hostmanager.common.utils;

import fr.milekat.hostmanager.api.classes.Instance;

public interface EventCaller {
    CommonEvent callEvent(CommonEvent.EventName eventName, Instance instance);
}
