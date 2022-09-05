package fr.milekat.hostmanager.common.hosts.adapter.pterodactyl;

import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.common.utils.Task;

import java.util.concurrent.TimeUnit;

public class RequestRetry {
    /**
     * Max requests retry (Every 2 seconds)
     */
    private final int maxRetry = 12;

    private Task task;

    /**
     * Task to try to setupStaff (May need a bit of time after the server creation)
     */
    public void setupStaff(String identifier) {
        task = Main.schedule(new Runnable() {
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
                        Main.getLogger().warn("Error while trying setup server ranks");
                        cancel();
                    } else {
                        return;
                    }
                }
                cancel();
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Cancel the task
     */
    private void cancel() {
        task.cancel();
    }
}
