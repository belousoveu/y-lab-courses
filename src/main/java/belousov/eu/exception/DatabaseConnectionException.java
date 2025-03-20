package belousov.eu.exception;

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(String jdbcUrl, String jdbcUser, String jdbcPassword, Throwable cause) {
        super(String.format("Не удалось подключиться к базе данных. jdbcUrl: %s, jdbcUser: %s, jdbcPassword: %s", jdbcUrl, jdbcUser, jdbcPassword), cause);
    }

}
