package fr.milekat.hostmanager.hosts.workers;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.InstanceState;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;

public class HostRemover {
    public HostRemover() {
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> {
            try {
                Main.getStorage()
                        .getActiveInstances()
                        .stream()
                        .filter(instance -> instance.getState().equals(InstanceState.ENDING))
                        .forEach(instance -> {
                            try {
                                Main.getHosts().deleteServer(instance);
                            } catch (HostExecuteException exception) {
                                if (Main.DEBUG) {
                                    Main.getHostLogger().warning("Error while trying to delete: " +
                                            instance.getName());
                                    exception.printStackTrace();
                                }
                            }
                        });
            } catch (StorageExecuteException exception) {
                Main.getHostLogger().severe("Error while trying to fetch active instances for remove check");
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}
