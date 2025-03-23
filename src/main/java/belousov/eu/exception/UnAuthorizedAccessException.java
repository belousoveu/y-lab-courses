package belousov.eu.exception;

public class UnAuthorizedAccessException extends RuntimeException {
    public UnAuthorizedAccessException() {

        super("Для доступа к данному ресурсу необходимо авторизоваться");
    }
}
