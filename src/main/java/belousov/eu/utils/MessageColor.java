package belousov.eu.utils;

public enum MessageColor {
    RED ("\u001B[31m"),
    BLUE("\u001B[34m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    CYAN("\u001B[36m"),
    PURPLE("\u001B[35m"),
    WHITE("\u001B[37m");

    private final String color;
    private static final String RESET = "\u001B[0m";

    MessageColor(String color) {
        this.color = color;
    }

    public String colored(String text) {
        return color + text + RESET;
    }


}
