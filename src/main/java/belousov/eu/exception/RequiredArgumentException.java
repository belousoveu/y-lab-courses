package belousov.eu.exception;

import java.util.List;

public class RequiredArgumentException extends RuntimeException {
    public RequiredArgumentException(List<String> messages) {
        super(String.format("Отсутствуют обязательные параметры: %s", String.join(", ", messages)));
    }

}
