package belousov.eu.utils;

import java.util.regex.Pattern;

public enum InputPattern {
    EMAIL(Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")),
    NAME(Pattern.compile("[a-zA-Zа-яА-ЯёЁ]{2,}")),
    PASSWORD(Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")),
    INTEGER(Pattern.compile("^\\d+$")),
    POSITIVE_INTEGER(Pattern.compile("^[1-9]\\d*$")),
    DECIMAL(Pattern.compile("^\\d+(\\.\\d+)?$")),
    POSITIVE_DECIMAL(Pattern.compile("^[1-9]\\d*(\\.\\d+)?$")),
    SUM(Pattern.compile("^[1-9]\\d*(\\.\\d{1,2})?$"));



    private final Pattern pattern;

    InputPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }
}
