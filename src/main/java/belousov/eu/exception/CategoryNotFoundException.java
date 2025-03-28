package belousov.eu.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(int id) {
        super(String.format("Не найдена категория с идентификатором %d", id));
    }

    public CategoryNotFoundException(String name, int userId) {
        super(String.format("Не найдена категория с названием \"%s\" пользователя с идентификатором %d", name, userId));
    }
}
