package fr.milekat.infra.manager.common.utils;

import fr.milekat.infra.manager.common.Main;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Configs {
    private final File configs;

    public Configs(File configs) {
        this.configs = configs;
    }

    private List<String> getKeys(String node) {
        if (node.contains(".")) {
            return Arrays.asList(node.split("\\."));
        } else {
            return Collections.singletonList(node);
        }
    }

    private @Nullable ConfigurationNode getValue(String node) {
        try {
            ConfigurationNode valueNode = YAMLConfigurationLoader.builder().setFile(configs).build().load();
            for (String key : getKeys(node)) {
                valueNode = valueNode.getNode(key);
            }
            return valueNode;
        } catch (IOException ignored) {
            if (Main.DEBUG) {
                Main.getLogger().warn("Config node not found: " + node);
            }
            return null;
        }
    }

    public @NotNull Integer getInt(String node) {
        return Integer.valueOf(getString(node));
    }

    public @NotNull Boolean getBoolean(String node) {
        return Objects.requireNonNull(getValue(node)).getBoolean();
    }

    public @NotNull String getString(String node) {
        return Objects.requireNonNull(getValue(node)).getString("");
    }

    public @NotNull List<?> getList(String node) {
        List<?> list = new ArrayList<>();
        Object obj = Objects.requireNonNull(getValue(node)).getValue();
        if (obj != null && obj.getClass().isArray()) {
            list = Arrays.asList((Object[]) obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>) obj);
        }
        return list;
    }

    public @NotNull List<String> getStringList(String node) {
        return getList(node).stream().map(Object::toString).collect(Collectors.toList());
    }
}
