package fr.milekat.infra.manager.common.storage.adapter.sql;

import fr.milekat.infra.manager.common.storage.adapter.sql.hikari.HikariPool;
import fr.milekat.infra.manager.common.storage.adapter.sql.hikari.MariaDBPool;
import fr.milekat.infra.manager.common.storage.adapter.sql.hikari.MySQLPool;
import fr.milekat.infra.manager.common.storage.adapter.sql.hikari.PostgresPool;
import fr.milekat.infra.manager.common.storage.exeptions.StorageLoaderException;
import fr.milekat.infra.manager.common.utils.Configs;
import org.jetbrains.annotations.NotNull;

public class SQLConnection {
    private final SQLDataBaseConnection sqlDataBaseConnection;

    public SQLConnection(@NotNull Configs config) throws StorageLoaderException {
        HikariPool hikariPool;
        switch (config.getString("storage.type").toLowerCase()) {
            case "mysql": {
                hikariPool = new MySQLPool();
                break;
            }
            case "mariadb": {
                hikariPool = new MariaDBPool();
                break;
            }
            case "postgres": {
                hikariPool = new PostgresPool();
                break;
            }
            default: {
                throw new StorageLoaderException("Unknown SQL type");
            }
        }
        hikariPool.init(config);
        sqlDataBaseConnection = hikariPool;
    }

    public SQLDataBaseConnection getSqlDataBaseConnection() {
        return sqlDataBaseConnection;
    }
}
