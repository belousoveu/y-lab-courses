package belousov.eu.exception;

import belousov.eu.utils.InputPattern;

public class InputFormatException extends RuntimeException {
    public InputFormatException(InputPattern pattern) {

        super("Ошибка ввода данных. %s".formatted(pattern.getMessage()));
    }

    public InputFormatException(String valuesString) {
        super("Ошибка ввода данных. Допустимые значения: %s".formatted(valuesString));
    }

}
