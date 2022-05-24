package fr.milekat.hostmanager.api.classes;

import java.util.Date;

public class Instance {
    private final String name;
    private String serverId;
    private int port;
    private InstanceState state;
    private final Game game;
    private final User host;
    private final Date creation;
    private final Date deletion;

    public Instance(String name, Game game, User host) {
        this.name = name;
        this.game = game;
        this.host = host;
        this.creation = new Date();
        this.deletion = null;
    }

    public Instance(String name, String serverId, int port, InstanceState state, Game game, User host, Date creation, Date deletion) {
        this.name = name;
        this.serverId = serverId;
        this.port = port;
        this.state = state;
        this.game = game;
        this.host = host;
        this.creation = creation;
        this.deletion = deletion;
    }

    public String getName() {
        return name;
    }

    public String getServerId() {
        return serverId;
    }

    public int getPort() {
        return port;
    }

    public InstanceState getState() {
        return state;
    }

    public Game getGame() {
        return game;
    }

    public User getHost() {
        return host;
    }

    public Date getCreation() {
        return creation;
    }

    public Date getDeletion() {
        return deletion;
    }
}
