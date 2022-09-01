package fr.milekat.hostmanager.common.utils;

import java.util.concurrent.TimeUnit;

public interface Scheduler {
    Task newSchedule(Runnable task, long delay, long period, TimeUnit unit);
}
