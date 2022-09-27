package fr.milekat.infra.manager.common.storage.adapter.sql.hikari;

import com.zaxxer.hikari.HikariConfig;
import fr.milekat.infra.manager.common.Main;
import org.jetbrains.annotations.NotNull;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;

public class MariaDBPool extends HikariPool {
    @Override
    public String getImplementationName() {
        return "MariaDB";
    }

    @Override
    protected void configureDatabase(@NotNull HikariConfig config, String address, String port,
                                     String databaseName, String username, String password) {
        try {
            MariaDbDataSource mariaDbDataSource = new MariaDbDataSource();
            mariaDbDataSource.setUrl("jdbc:mariadb://" + address + ":" + port + "/" + databaseName);
            mariaDbDataSource.setUser(username);
            mariaDbDataSource.setPassword(password);
            config.setDataSource(mariaDbDataSource);
        } catch (SQLException exception) {
            Main.getLogger().warn("MariaDBPool error");
            exception.printStackTrace();
        }
    }
}
