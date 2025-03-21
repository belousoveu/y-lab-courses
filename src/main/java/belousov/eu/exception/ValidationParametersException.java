package belousov.eu.exception;

public class ValidationParametersException extends RuntimeException {
    public ValidationParametersException(String violations) {

        super(String.format("Указанные данные некорректны: %s", violations));
    }
}
