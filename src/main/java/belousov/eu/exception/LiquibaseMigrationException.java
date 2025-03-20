package belousov.eu.exception;

public class LiquibaseMigrationException extends RuntimeException {
    public LiquibaseMigrationException(Throwable cause) {

        super("Ошибка при выполнении миграции Liquibase", cause);
    }
}
