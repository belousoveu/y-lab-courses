package belousov.eu.view;

import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;

import java.util.Scanner;

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
}
