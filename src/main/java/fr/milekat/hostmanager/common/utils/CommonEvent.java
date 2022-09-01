package fr.milekat.hostmanager.common.utils;

public interface CommonEvent {
    void setCancel(boolean cancel);

    boolean isCancelled();

    enum EventName {
        ServerCreatedEvent,
        ServerDeletedEvent,
        ServerDeletionEvent
    }
}
