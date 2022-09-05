package fr.milekat.hostmanager.common.hosts.adapter.pterodactyl;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.HostExecutor;
import fr.milekat.hostmanager.common.hosts.exeptions.HostExecuteException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PterodactylAdapter implements HostExecutor {
    /**
     * Check if pterodactyl API is reachable
     * Warning ! This method will block the thread until both keys are checked !
     */
    @Override
    public boolean checkAvailability() throws HostExecuteException {
        //  Check user key access
        try {
            PterodactylRequests.getClientServers();
        } catch (HostExecuteException exception) {
            throw new HostExecuteException(exception, "Check your panel account key");
        }
        //  Check Admin key ALLOCATIONS PERMISSIONS
        try {
            PterodactylRequests.getAdminAllocations();
        } catch (HostExecuteException exception) {
            throw new HostExecuteException(exception, "Check your admin key, and allocation access");
        }
        //  Check Admin key SERVERS PERMISSIONS
        try {
            PterodactylRequests.getAdminServers();
        } catch (HostExecuteException exception) {
            throw new HostExecuteException(exception, "Check your admin key, and server access");
        }
        return true;
    }

    /**
     * Create a pterodactyl server !
     * @param instance server params
     */
    @Override
    public void createServer(Instance instance) throws HostExecuteException {
        JSONObject server = PterodactylRequests.setupServer(instance);
        if (server.has("attributes")) {
            JSONObject attributes = server.getJSONObject("attributes");
            instance.setServerId(String.valueOf(attributes.getInt("id")));
            instance.setName(instance.getName() + "_#" + instance.getServerId());
            try {
                instance.setCreation(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        .parse(attributes.getString("created_at")
                                .replaceAll("\\+00:00$", "")
                        )
                );
                new RequestRetry().setupStaff(attributes.getString("identifier"));
            } catch (ParseException exception) {
                if (Main.DEBUG) {
                    exception.printStackTrace();
                }
                Main.getLogger().warn("Error while trying to parse date: " +
                        attributes.getString("created_at"));
                instance.setCreation(new Date());
            }
        } else {
            instance.setCreation(new Date());
            instance.setServerId(null);
        }
    }

    /**
     * Delete a pterodactyl server !
     * @param instance server params
     */
    @Override
    public void deleteServer(Instance instance) throws HostExecuteException {
        try {
            PterodactylRequests.deleteServer(instance);
        } catch (HostExecuteException exception) {
            if (!exception.getMessage().contains("Server not found ?")) {
                throw new HostExecuteException(exception, "Can't delete server " + instance.getName());
            }
        }
    }
}
