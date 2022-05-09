package fr.milekat.hostmanager.api.classes;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private final String lastName;
    private final int tickets;

    public User(UUID uuid, String lastName, int tickets) {

        this.uuid = uuid;
        this.lastName = lastName;
        this.tickets = tickets;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLastName() {
        return lastName;
    }

    public int getTickets() {
        return tickets;
    }
}
