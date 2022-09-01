package fr.milekat.hostmanager.bungeecord.utils;

import fr.milekat.hostmanager.common.utils.Scheduler;
import fr.milekat.hostmanager.common.utils.Task;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class BungeeScheduler implements Scheduler {
    private final Plugin plugin;

    public BungeeScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public Task newSchedule(Runnable task, long delay, long period, TimeUnit unit) {
        return new Scheduled(plugin, task, delay, period, unit);
    }

    static class Scheduled implements Task {
        private final ScheduledTask task;

        public Scheduled(Plugin plugin, Runnable task, long delay, long period, TimeUnit unit) {
            this.task = ProxyServer.getInstance().getScheduler().schedule(plugin, task, delay, period, unit);
        }

        public void cancel() {
            task.cancel();
        }
    }
}
