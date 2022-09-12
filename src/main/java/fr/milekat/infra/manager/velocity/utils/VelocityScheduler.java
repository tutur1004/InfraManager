package fr.milekat.infra.manager.velocity.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import fr.milekat.infra.manager.common.utils.Scheduler;
import fr.milekat.infra.manager.common.utils.Task;
import fr.milekat.infra.manager.velocity.MainVelocity;

import java.util.concurrent.TimeUnit;

public class VelocityScheduler implements Scheduler {
    private final MainVelocity plugin;
    private final ProxyServer server;

    public VelocityScheduler(MainVelocity plugin, ProxyServer server) {
        this.plugin = plugin;
        this.server = server;
    }

    /**
     * Schedule a new {@link Scheduled} Velocity task
     */
    @Override
    public Task newSchedule(Runnable task, long delay, long period, TimeUnit unit) {
        return new VelocityScheduler.Scheduled(plugin, server, task, delay, period, unit);
    }

    static class Scheduled implements Task {
        private final ScheduledTask task;

        public Scheduled(MainVelocity plugin, ProxyServer server, Runnable task, long delay, long period, TimeUnit unit) {
            this.task = server.getScheduler()
                    .buildTask(plugin, task)
                    .delay(delay, unit)
                    .repeat(period, unit)
                    .schedule();
        }

        /**
         * Cancel this {@link Scheduled} Velocity task
         */
        @Override
        public void cancel() {
            task.cancel();
        }
    }
}
