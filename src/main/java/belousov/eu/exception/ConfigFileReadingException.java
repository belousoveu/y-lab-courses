package belousov.eu.exception;

public class ConfigFileReadingException extends RuntimeException {
    public ConfigFileReadingException(String message, Throwable cause) {
        super(String.format("Ошибка чтения файла конфигурации: %s", message), cause);
    }
}
