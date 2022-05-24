package fr.milekat.hostmanager.hosts;

import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
//import fr.milekat.hostmanager.hosts.pterodactyl.PterodactylAdapter;
import net.md_5.bungee.config.Configuration;

public class HostsManager {
    //private final HostExecutor hostExecutor;

    public HostsManager(Configuration config) throws HostExecuteException {
        if (config.getString("host.provider").equalsIgnoreCase("pterodactyl")) {
            //hostExecutor = new PterodactylAdapter();
        } else {
            throw new HostExecuteException("Unsupported database type");
        }
        /*if (!hostExecutor.checkAvailability()) {
            throw new HostExecuteException("Storages are not loaded properly");
        }*/
    }

    public HostExecutor getHostExecutor() {
        return null;
    }
}