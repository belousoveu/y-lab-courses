package belousov.eu.utils;

import lombok.Getter;

import java.util.regex.Pattern;

public enum InputPattern {
    EMAIL(Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"), "Необходимо указать корректный адрес электронной почты"),
    NAME(Pattern.compile("[a-zA-Zа-яА-ЯёЁ -]{2,}"), "Допустимо использовать только буквы, пробел и дефис, длина от 2 символов"),
    PASSWORD(Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"), "Пароль должен содержать минимум 8 символов, включая буквы и цифры"),
    INTEGER(Pattern.compile("^\\d+$"), "Необходимо указать целое число"),
    POSITIVE_INTEGER(Pattern.compile("^[1-9]\\d*$"), "Необходимо указать положительное целое число"),
    DECIMAL(Pattern.compile("^\\d+(\\.\\d+)?$"), "Необходимо указать действительное число"),
    POSITIVE_DECIMAL(Pattern.compile("^[1-9]\\d*(\\.\\d+)?$"), "Необходимо указать положительное действительное число"),
    SUM(Pattern.compile("^[1-9]\\d*(\\.\\d{1,2})?$"), "Необходимо указать сумму в формате от 1 до 999999999999999.99, с точностью до двух знаков после запятой"),
    YEAR_MONTH(Pattern.compile("^(20\\d\\d)-(0[1-9]|1[0-2])$"), "Необходимо указать дату в формате YYYY-MM"),
    DATE(Pattern.compile("^(20\\d\\d)-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"), "Необходимо указать дату в формате YYYY-MM-DD");



    private final Pattern pattern;
    @Getter
    private final String message;

    InputPattern(Pattern pattern, String message) {
        this.pattern = pattern;
        this.message = message;
    }

    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }
}
