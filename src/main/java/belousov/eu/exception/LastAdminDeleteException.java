package belousov.eu.exception;

public class LastAdminDeleteException extends RuntimeException {
    public LastAdminDeleteException() {

        super("Нельзя удалить последнего администратора");
    }
}
