package belousov.eu.exception;

public class HibernateConfigException extends RuntimeException {
    public HibernateConfigException(Throwable cause) {

        super("Ошибка инициализации Hibernate.", cause);
    }
}
