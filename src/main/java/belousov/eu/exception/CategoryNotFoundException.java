package belousov.eu.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(int id) {
        super(String.format("Не найдена категория с идентификатором %d", id));
    }
}
