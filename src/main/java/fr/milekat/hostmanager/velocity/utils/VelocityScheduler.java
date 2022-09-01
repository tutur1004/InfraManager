package fr.milekat.hostmanager.velocity.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import fr.milekat.hostmanager.common.utils.Scheduler;
import fr.milekat.hostmanager.common.utils.Task;

import java.util.concurrent.TimeUnit;

public class VelocityScheduler implements Scheduler {
    private final ProxyServer server;

    public VelocityScheduler(ProxyServer server) {
        this.server = server;
    }

    public Task newSchedule(Runnable task, long delay, long period, TimeUnit unit) {
        return new VelocityScheduler.Scheduled(server, task, delay, period, unit);
    }

    static class Scheduled implements Task {
        private final ScheduledTask task;

        public Scheduled(ProxyServer server, Runnable task, long delay, long period, TimeUnit unit) {
            this.task = server.getScheduler()
                    .buildTask(server, task)
                    .delay(delay, unit)
                    .repeat(period, unit)
                    .schedule();
        }

        public void cancel() {
            task.cancel();
        }
    }
}
