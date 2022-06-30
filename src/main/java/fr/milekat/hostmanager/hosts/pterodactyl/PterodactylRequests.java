package fr.milekat.hostmanager.hosts.pterodactyl;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.stream.Collectors;

public class PterodactylRequests extends HttpExecute {
    private static final String ENDPOINT = Main.getFileConfig().getString("host.pterodactyl.endpoint");
    private static final String KEY = Main.getFileConfig().getString("host.pterodactyl.admin.key");

    /**
     * Create a pterodactyl server
     * @param instance representation of the desired server
     */
    public static JSONObject setupServer(Instance instance) throws HostExecuteException {
        try {
            JSONObject envVars = new JSONObject();
            envVars.put(Main.HOST_UUID_ENV_VAR_NAME, instance.getHost().getUuid());
            envVars.put("CONFIG", instance.getGame().getConfigs().keySet()
                    .stream()
                    .map(key -> key + "=" + instance.getGame().getConfigs().get(key))
                    .collect(Collectors.joining(";")));
            return execute(new URL(ENDPOINT + "/api/application/servers"), "POST", KEY,
                    new JSONObject().put("name", instance.getName())
                            .put("description", instance.getDescription())
                            .put("user", Main.getFileConfig().getString("host.pterodactyl.account.id"))
                            .put("egg", Main.getFileConfig().getString("host.pterodactyl.egg"))
                            .put("docker_image", instance.getGame().getImage())
                            .put("startup", Main.getFileConfig().getString("host.command")
                                    .replaceAll("\\{MEM}", String.valueOf(instance.getGame().getRequirements())))
                            .put("environment", envVars)
                            .put("limits", new JSONObject().put("memory", instance.getGame().getRequirements())
                                    .put("swap", 0).put("disk", 1024).put("io", 500).put("cpu", 0))
                            .put("feature_limits", new JSONObject().put("databases", 0).put("backups", 0))
                            .put("allocation", new JSONObject().put("default", setupAllocation(instance.getPort())))
                            .put("start_on_completion", true)
                            .toString());
        } catch (IOException exception) {
            throw new HostExecuteException(exception, "Pterodactyl API error, URL IOException ?");
        } catch (AllocationAlreadyUsed exception) {
            throw new HostExecuteException(exception, "Pterodactyl API error, allocation port already used.");
        }
    }

    /**
     * Delete a server from
     * @param instance representation of the desired server (At least the server id)
     */
    public static void deleteServer(Instance instance) throws HostExecuteException {
        try {
            execute(new URL(ENDPOINT + "/api/application/servers/" + instance.getServerId() + "/force"),
                    "DELETE", KEY, null);
        } catch (IOException exception) {
            throw new HostExecuteException(exception, "Pterodactyl API error, URL IOException ?");
        }
    }

    /**
     * Create an allocation on this port
     * @param port allocation port
     * @return allocation id
     * @throws AllocationAlreadyUsed if ...
     */
    public static int setupAllocation(int port) throws AllocationAlreadyUsed, HostExecuteException {
        String ip = "127.0.0.1";
        String node = Main.getFileConfig().getString("host.pterodactyl.node");

        //  Try to retrieve this allocation (If already exist)
        int allocation = retrieveAllocation(ip, port, node);
        if (allocation !=0) return allocation;

        //  If not, create this allocation
        try {
            execute(new URL(ENDPOINT + "/api/application/nodes/" + node + "/allocations"), "POST", KEY,
                new JSONObject().put("ip", ip).put("ports", Collections.singleton(port)).toString());
            return retrieveAllocation(ip, port, node);
        } catch (IOException exception) {
            throw new AllocationAlreadyUsed(exception); // TODO: 24/05/2022 Check this point
        }
    }

    /**
     * Retrieve an allocation if exist
     * @param ip allocation ip
     * @param port allocation port
     * @param node allocation node name
     * @return allocation id
     * @throws AllocationAlreadyUsed Port is already assign or connection issue
     */
    private static int retrieveAllocation(String ip, int port, String node) throws AllocationAlreadyUsed {
        try {
            JSONObject allocations = execute(new URL(ENDPOINT + "/api/application/nodes/" + node + "/allocations"),
                    "GET", KEY, null);
            JSONArray getData = allocations.getJSONArray("data");
            for (int i = 0; i < getData.length(); i++) {
                JSONObject jsonAllocation = getData.getJSONObject(i);
                if (jsonAllocation.has("attributes")) {
                    JSONObject attributes = jsonAllocation.getJSONObject("attributes");
                    if (attributes.getString("ip").equalsIgnoreCase(ip) && attributes.getInt("port") == port) {
                        if (attributes.getBoolean("assigned")) {
                            throw new AllocationAlreadyUsed(new Throwable("Port already assign"));
                        }
                        return attributes.getInt("id");
                    }
                }
            }
            return 0;
        } catch (HostExecuteException | IOException exception) {
            throw new AllocationAlreadyUsed(exception);
        }
    }

    private static class AllocationAlreadyUsed extends Exception {
        public AllocationAlreadyUsed(Throwable exception) {
            super(exception);
        }
    }
}
