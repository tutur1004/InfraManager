package fr.milekat.hostmanager.api.classes;

import net.md_5.bungee.api.ProxyServer;

import java.util.Date;

public class Game {
    private final Integer id;
    private final String name;
    private final Date create;
    private final boolean enable;
    private final String gameVersion;
    private final String serverVersion;
    private final String image;
    private final int requirements;

    public Game(String name, boolean enable, String image, int requirements) {
        this.id = null;
        this.name = name;
        this.create = new Date();
        this.enable = enable;
        this.gameVersion = null;
        this.serverVersion = ProxyServer.getInstance().getVersion();
        this.image = image;
        this.requirements = requirements;
    }

    public Game(Integer id, String name, Date create, boolean enable, String gameVersion, String serverVersion, String image, int requirements) {
        this.id = id;
        this.name = name;
        this.create = create;
        this.enable = enable;
        this.gameVersion = gameVersion;
        this.serverVersion = serverVersion;
        this.image = image;
        this.requirements = requirements;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreate() {
        return create;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getImage() {
        return image;
    }

    public int getRequirements() {
        return requirements;
    }
}
