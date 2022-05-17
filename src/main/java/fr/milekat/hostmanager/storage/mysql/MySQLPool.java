package fr.milekat.hostmanager.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLPool {
    private static HikariDataSource ds;

    public MySQLPool(Configuration config) {
        HikariConfig hConfig = new HikariConfig();
        if (config.getString("database.type").equalsIgnoreCase("mysql")) {
            hConfig.setDriverClassName("com.mysql.jdbc.Driver");
            hConfig.setJdbcUrl( "jdbc:mysql://" +
                    config.getString("database.mysql.hostname") + "/" +
                    config.getString("database.mysql.database"));
        } else if (config.getString("database.type").equalsIgnoreCase("mariadb")) {
            hConfig.setDriverClassName("org.mariadb.jdbc.Driver");
            hConfig.setJdbcUrl( "jdbc:mariadb://" +
                    config.getString("database.mysql.hostname") + "/" +
                    config.getString("database.mysql.database"));
        }
        hConfig.setUsername(config.getString("database.mysql.username"));
        hConfig.setPassword(config.getString("database.mysql.password"));
        hConfig.addDataSourceProperty("cachePrepStmts", "true");
        hConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(hConfig);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void disconnect() {
        ds.close();
    }
}
