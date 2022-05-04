package fr.milekat.hostmanager.hosts.classes;

import java.util.Date;

public class Game {
    private final int id;
    private final String name;
    private final Date create_date;
    private final boolean enable;
    private final String image;
    private final int requirements;

    public Game(String name, boolean enable, String image, int requirements) {
        this.id = 0;
        this.name = name;
        this.create_date = new Date();
        this.enable = enable;
        this.image = image;
        this.requirements = requirements;
    }

    public Game(int id, String name, Date create_date, boolean enable, String image, int requirements) {
        this.id = id;
        this.name = name;
        this.create_date = create_date;
        this.enable = enable;
        this.image = image;
        this.requirements = requirements;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getImage() {
        return image;
    }

    public int getRequirements() {
        return requirements;
    }
}
