package belousov.eu.config;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class LiquibaseConfig {

    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPassword;
    private final String changeLogFile;
    private final String schemaName;
    private final String defaultSchemaName;

    public LiquibaseConfig(Map<String, Object> properties) {
        this.jdbcUrl = properties.get("hibernate.connection.url").toString();
        this.jdbcUser = properties.get("hibernate.connection.username").toString();
        this.jdbcPassword = properties.get("hibernate.connection.password").toString();

        this.changeLogFile = properties.get("liquibase.changeLogName").toString();
        this.schemaName = properties.get("liquibase.schemaName").toString();
        this.defaultSchemaName = properties.get("liquibase.defaultSchemaName").toString();
    }

    public void runMigration() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            Database db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            db.setDefaultSchemaName(defaultSchemaName);
            db.setLiquibaseSchemaName(schemaName);

            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), db);
            liquibase.update(new Contexts());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
