package fr.milekat.hostmanager.common.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Configs {
    private final InputStream configs;

    public Configs(InputStream configs) {
        this.configs = configs;
    }

    private List<String> getKeys(String node) {
        return Arrays.asList(node.split(node, '.'));
    }

    @Nullable
    private Object getValue(String node) {
        Object value = null;
        for (String key : getKeys(node)) {
            Map<String, Object> keyValue = new Yaml().load(configs);
            value =  keyValue.get(key);
        }
        return value;
    }

    @NotNull
    public Boolean getBoolean(String node) {
        Object bool = getValue(node);
        return bool != null && (Boolean) bool;
    }

    @NotNull
    public String getString(String node) {
        return String.valueOf(getValue(node));
    }

    @NotNull
    public List<?> getList(String node) {
        List<?> list = new ArrayList<>();
        Object obj = getValue(node);
        if (obj != null && obj.getClass().isArray()) {
            list = Arrays.asList((Object[]) obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>) obj);
        }
        return list;
    }

    @NotNull
    public List<String> getStringList(String node) {
        return getList(node).stream().map(Object::toString).collect(Collectors.toList());
    }
}
