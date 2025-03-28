package belousov.eu.exception;

public class PathNotFoundException extends RuntimeException {
    public PathNotFoundException(String path) {

        super(String.format("Не найден путь %s", path));
    }
}
