package fr.milekat.infra.manager.common.hosts.adapter.pterodactyl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PterodactylUsersPermissions {
    admin(Arrays.asList("websocket.connect",
            "control.console", "control.start", "control.stop", "control.restart",
            "file.create", "file.read", "file.read-content", "file.update", "file.delete", "file.archive")),
    moderator(Arrays.asList("websocket.connect",
            "control.console", "control.start", "control.stop", "control.restart")),
    watchers(Collections.singletonList("websocket.connect"));

    private final List<String> permissions;

    PterodactylUsersPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
