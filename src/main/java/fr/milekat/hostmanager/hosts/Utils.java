package fr.milekat.hostmanager.hosts;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.hosts.bungee.ServerManager;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    /**
     * Load all hosts in bungee-cord server list
     */
    public static void resetBungeeHostList() throws StorageExecuteException {
        ServerManager.removeServersPrefix(Main.HOST_BUNGEE_SERVER_PREFIX);
        for (Instance server : Main.getStorage().getActiveInstances()) {
            ServerManager.addServer(Main.HOST_BUNGEE_SERVER_PREFIX + server.getName(), server.getPort());
        }
    }

    /**
     * Get env vars formatted as intend
     */
    public static Map<String, String> getEnvVars(Instance instance) {
        Map<String, String> envVars = new HashMap<>();
        envVars.put(Main.HOST_UUID_ENV_VAR_NAME, instance.getHost().getUuid().toString());
        envVars.put("GAME", instance.getGame().getName());
        envVars.put("VERSION", instance.getGame().getGameVersion());
        envVars.put("CONFIGPARSER", instance.getGame().getConfigs().keySet()
                .stream()
                .map(key -> key + "=" + instance.getGame().getConfigs().get(key))
                .collect(Collectors.joining(";")));
        return envVars;
    }

    /**
     * Get an available port
     */
    public static Integer getAvailablePort() throws StorageExecuteException {
        return Main.getStorage().findAvailablePort(Main.getFileConfig()
                .getList("host.settings.ports")
                .stream()
                .map(o -> Integer.valueOf(o.toString()))
                .collect(Collectors.toList()));
    }
}
