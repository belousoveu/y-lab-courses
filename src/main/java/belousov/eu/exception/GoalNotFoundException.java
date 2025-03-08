package belousov.eu.exception;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(int id) {

        super(String.format("Не найдена цель с идентификатором %d", id));
    }
}
