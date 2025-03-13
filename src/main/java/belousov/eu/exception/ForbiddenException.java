package belousov.eu.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("Недостаточно прав для выполнения операции");
    }
}
