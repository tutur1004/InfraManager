package fr.milekat.infra.manager.common.storage.adapter.sql;

import fr.milekat.infra.manager.common.utils.Configs;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDataBaseConnection {
    String getImplementationName();

    void init(@NotNull Configs configs);

    void close();

    Connection getConnection() throws SQLException;
}
