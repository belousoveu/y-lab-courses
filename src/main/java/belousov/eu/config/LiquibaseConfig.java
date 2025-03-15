package belousov.eu.config;

import belousov.eu.exception.DatabaseConnectionException;
import belousov.eu.exception.LiquibaseMigrationException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
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

    private LiquibaseConfig(Map<String, Object> properties) {
        this.jdbcUrl = properties.get("hibernate.connection.url").toString();
        this.jdbcUser = properties.get("hibernate.connection.username").toString();
        this.jdbcPassword = properties.get("hibernate.connection.password").toString();

        this.changeLogFile = properties.get("liquibase.changeLogName").toString();
        this.schemaName = properties.get("liquibase.schemaName").toString();
        this.defaultSchemaName = properties.get("liquibase.defaultSchemaName").toString();
    }

    public static void initialize(Map<String, Object> properties) {
        new LiquibaseConfig(properties).runMigration();
    }

    private void runMigration() {

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)) {
            Database db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            db.setDefaultSchemaName(defaultSchemaName);
            db.setLiquibaseSchemaName(schemaName);

            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), db);
            liquibase.update(new Contexts());
        } catch (SQLException e) {
            throw new DatabaseConnectionException(jdbcUrl, jdbcUser, jdbcPassword, e);
        } catch (LiquibaseException e) {
            throw new LiquibaseMigrationException(e);
        }
    }
}
