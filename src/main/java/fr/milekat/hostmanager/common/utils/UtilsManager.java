package fr.milekat.hostmanager.common.utils;

public interface UtilsManager {
    /**
     * Get InstanceEvent
     * @return {@link EventCaller}
     */
    EventCaller getInstanceEvent();

    /**
     * Get Scheduler
     * @return {@link Scheduler}
     */
    Scheduler getScheduler();

    /**
     * Get ServerManager
     * @return {@link HostUtils}
     */
    HostUtils getHostUtils();
}
