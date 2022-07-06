package fr.milekat.hostmanager.hosts.pterodactyl;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class RequestRetry {
    /**
     * Max requests retry (Every 2 seconds)
     */
    private final int maxRetry = 12;

    private ScheduledTask task;

    /**
     * Task to try to setupStaff (May need a bit of time after the server creation)
     */
    public void setupStaff(String identifier) {
        task = ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable() {
            int counter = 0;
            @Override
            public void run() {
                counter++;
                try {
                    PterodactylRequests.setupStaff(identifier);
                } catch (HostExecuteException exception) {
                    if (counter >= maxRetry) {
                        if (Main.DEBUG) {
                            exception.printStackTrace();
                        }
                        Main.getHostLogger().warning("Error while trying setup server ranks");
                        cancel();
                    } else {
                        return;
                    }
                }
                cancel();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Cancel the task
     */
    private void cancel() {
        task.cancel();
    }
}
