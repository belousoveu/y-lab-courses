package belousov.eu.view;

import belousov.eu.model.User;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.function.Function;

public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);

    public String readString(String prompt) {
        System.out.println(prompt);
        return readString();
    }

    public String readString(String prompt, InputPattern pattern) {
        System.out.println(prompt);
        return readString(pattern);
    }

    public String readString(InputPattern pattern) {
        String input = readString();
        if (!pattern.matches(input)) {
            throw new IllegalArgumentException("Неверный формат"); //TODO custom exception
        }
        return input;
    }

    public String readString() {
        return scanner.nextLine();
    }

    public void println(String message) {
        System.out.println(message);
    }

    public void println(String message, MessageColor color) {
        System.out.println(color.colored(message));
    }

    public <T> void println(String title, List<T> items, MessageColor colorTitle, MessageColor colorItem) {
        println(title, colorTitle);
        println("=".repeat(title.length()), colorTitle);
        items.forEach(item -> println(item.toString(), colorItem));
    }

    public void printUser(User currentUser) {
        println("Профиль пользователя:");
        println("=====================");
        println("Имя: " + MessageColor.YELLOW.colored(currentUser.getName()));
        println("Email: " + MessageColor.YELLOW.colored(currentUser.getEmail()));
    }

    public int readInt(String prompt, InputPattern inputPattern) {
        String input = readString(prompt);
        if (!inputPattern.matches(input)) {
            throw new IllegalArgumentException("Неверный формат"); //TODO custom exception
        }
        return Integer.parseInt(input);
    }

    public int readInt(String prompt) {
        return readInt(prompt, InputPattern.INTEGER);
    }


    public Double readDouble(String prompt, InputPattern inputPattern) {
        String input = readString(prompt).trim().replaceAll("[_ ]", "");

        if (!inputPattern.matches(input)) {
            throw new IllegalArgumentException("Неверный формат"); //TODO custom exception
        }
        return Double.parseDouble(input);
    }

    public Double readDouble(String prompt) {
        return readDouble(prompt, InputPattern.DECIMAL);
    }

    public <T extends Temporal> T readPeriod(String prompt, InputPattern inputPattern, Function<String, T> parser) {
        String input = readString(prompt);
        if (!inputPattern.matches(input)) {
            throw new IllegalArgumentException("Неверный формат"); //TODO custom exception
        }
        return parser.apply(input);
    }


    public <E> E readFromList(String prompt, List<E> values) {
        String input = readString(prompt + valuesToString(values)).trim();
        for (E value : values) {
            if (value.toString().equalsIgnoreCase(input)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Неверный формат"); //TODO custom exception
    }

    private String valuesToString(List<?> values) {
        StringJoiner joiner = new StringJoiner(", ", " (", "): ");
        values.forEach(v -> joiner.add(v.toString()));
        return joiner.toString();
    }
}
