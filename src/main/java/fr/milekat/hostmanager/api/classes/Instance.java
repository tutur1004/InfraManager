package fr.milekat.hostmanager.api.classes;

import java.util.Date;

public class Instance {
    private final String name;
    private final String description;
    private String serverId;
    private int port;
    private InstanceState state;
    private final Game game;
    private final User host;
    private Date creation;
    private Date deletion;

    public Instance(String name, String serverId) {
        this.name = name;
        this.description = null;
        this.serverId = serverId;
        this.game = null;
        this.host = null;
    }

    public Instance(String name, Game game, User host) {
        this.name = name;
        this.description = "Server created by: " + host.getLastName();
        this.game = game;
        this.host = host;
    }

    public Instance(String name, String description, String serverId, int port, InstanceState state, Game game, User host, Date creation, Date deletion) {
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getPort() {
        return port;
    }

    public InstanceState getState() {
        return state;
    }

    public void setState(InstanceState state) {
        this.state = state;
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

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Date getDeletion() {
        return deletion;
    }

    public void setDeletion(Date deletion) {
        this.deletion = deletion;
    }
}
