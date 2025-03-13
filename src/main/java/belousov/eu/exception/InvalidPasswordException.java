package belousov.eu.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {

        super("Введен неверный пароль");
    }
}
