package fr.milekat.infra.manager.bungeecord.utils;

import fr.milekat.infra.manager.common.utils.Scheduler;
import fr.milekat.infra.manager.common.utils.Task;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class BungeeScheduler implements Scheduler {
    private final Plugin plugin;

    public BungeeScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Schedule a new {@link Scheduled} Bungee task
     */
    @Override
    public Task newSchedule(Runnable task, long delay, TimeUnit unit) {
        return new Scheduled(plugin, task, delay, unit);
    }

    /**
     * Schedule a new {@link Scheduled} Bungee task
     */
    @Override
    public Task newSchedule(Runnable task, long delay, long period, TimeUnit unit) {
        return new Scheduled(plugin, task, delay, period, unit);
    }

    static class Scheduled implements Task {
        private final ScheduledTask task;

        public Scheduled(Plugin plugin, Runnable task, long delay, TimeUnit unit) {
            this.task = ProxyServer.getInstance().getScheduler().schedule(plugin, task, delay, unit);
        }

        public Scheduled(Plugin plugin, Runnable task, long delay, long period, TimeUnit unit) {
            this.task = ProxyServer.getInstance().getScheduler().schedule(plugin, task, delay, period, unit);
        }

        /**
         * Cancel this {@link Scheduled} Bungee task
         */
        @Override
        public void cancel() {
            task.cancel();
        }
    }
}
