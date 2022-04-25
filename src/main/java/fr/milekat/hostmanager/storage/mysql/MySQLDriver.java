package fr.milekat.hostmanager.storage.mysql;

import fr.milekat.hostmanager.HostManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDriver {
    private Connection connection;
    private final String driver, url, host, database, user, pass;

    /**
     * Init MySQL Driver
     */
    public MySQLDriver(String url, String host, String database, String user, String pass) {
        this.driver = "com.mysql.jdbc.Driver";
        this.url = url;
        this.host = host;
        this.database = database;
        this.user = user;
        this.pass = pass;
    }

    /**
     * Connect to Database (With autoReconnect feature)
     */
    public void connection() {
        try {
            Class.forName(this.driver);
            connection = DriverManager.getConnection(url + host + "/" + database + "?autoReconnect=true&allowMultiQueries=true&rewriteBatchedStatements=true&characterEncoding=UTF-8", user, pass);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            if (HostManager.DEBUG) HostManager.getHostLogger().warning("SQL Error.");
        } catch (ClassNotFoundException throwable) {
            throwable.printStackTrace();
            if (HostManager.DEBUG) HostManager.getHostLogger().warning("Missing SQL Class.");
        }
    }

    /**
     * Disconnect the database
     */
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            if (HostManager.DEBUG) HostManager.getHostLogger().warning("SQL Error.");
        }
    }

    /**
     * Get the SQL Connection class
     */
    public Connection getConnection() { return connection; }
}
