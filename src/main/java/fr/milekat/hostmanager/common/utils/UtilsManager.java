package fr.milekat.hostmanager.common.utils;

public interface UtilsManager {
    EventCaller getInstanceEvent();

    Scheduler getScheduler();

    ServerUtils getServerManager();
}
