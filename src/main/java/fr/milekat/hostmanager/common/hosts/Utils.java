package fr.milekat.hostmanager.common.hosts;

import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.storage.exeptions.StorageExecuteException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    /**
     * Get all servers from config lobby list
     * @return a list of server names representing all lobby
     */
    public static List<String> getLobbyList() {
        return Main.getConfig().getStringList("host.settings.lobby-name");
    }

    /**
     * Get env vars formatted as intend
     */
    public static Map<String, String> getEnvVars(Instance instance) {
        Map<String, String> envVars = new HashMap<>();
        envVars.put(Main.HOST_UUID_ENV_VAR_NAME, instance.getHost().getUuid().toString());
        envVars.put("STARTUP", Main.getConfig().getString("host.settings.command"));
        envVars.put("MIN_MEMORY", instance.getGame().getRequirements() + "M");
        envVars.put("MAX_MEMORY", instance.getGame().getRequirements() + "M");
        envVars.put("PORT", String.valueOf(instance.getPort()));
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
        return Main.getStorage().findAvailablePort(Main.getConfig()
                .getList("host.settings.ports")
                .stream()
                .map(o -> Integer.valueOf(o.toString()))
                .collect(Collectors.toList()));
    }
}
