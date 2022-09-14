package fr.milekat.infra.manager.common.hosts.utils;

import fr.milekat.infra.manager.api.classes.InstanceState;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.exeptions.HostExecuteException;
import fr.milekat.infra.manager.common.storage.exeptions.StorageExecuteException;

import java.util.concurrent.TimeUnit;

public class HostGarbageCollector {
    // TODO: 05/09/2022 Implement more cases..
    public HostGarbageCollector() {
        Main.schedule( () -> {
            try {
                Main.getStorage()
                        .getActiveInstances()
                        .stream()
                        .filter(instance -> instance.getState().equals(InstanceState.ENDING))
                        .forEach(instance -> {
                            try {
                                Main.getHosts().deleteHost(instance);
                            } catch (HostExecuteException exception) {
                                if (Main.DEBUG) {
                                    Main.getLogger().warn("Error while trying to delete: " +
                                            instance.getName());
                                    exception.printStackTrace();
                                }
                            }
                        });
            } catch (StorageExecuteException exception) {
                Main.getLogger().trace("Error while trying to fetch active instances for remove check");
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
            }
        }, 1, 10, TimeUnit.SECONDS);
    }
}
