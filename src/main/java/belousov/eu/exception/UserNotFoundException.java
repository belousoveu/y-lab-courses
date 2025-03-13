package belousov.eu.exception;

public class UserNotFoundException extends RuntimeException {


    public UserNotFoundException(String emial) {
        super(String.format("Пользователь с электронной почтой %s не найден", emial));
    }

    public UserNotFoundException(int id) {
        super(String.format("Пользователь с идентификатором %s не найден", id));
    }
}
