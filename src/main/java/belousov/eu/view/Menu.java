package belousov.eu.view;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.controller.command.CommandMenu;
import belousov.eu.model.Role;
import belousov.eu.utils.MessageColor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {
    private final String title;
    private final Map<Integer, MenuItem> items = new LinkedHashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private final Role role;
    @Getter
    @Setter
    private Menu parent;

    public Menu(String title) {
        this.title = title;
        this.role = null;
        this.parent = null;

    }

    public Menu(String title, Role role) {
        this.title = title;
        this.role = role;
        this.parent = null;
    }

    public void add(int key, String title, CommandMenu command) {
        items.put(key, new MenuItem(title, command));
    }

    public void add(int key, String title, CommandMenu command, Role role) {
        items.put(key, new MenuItem(title, command, role));
    }

    public void display() {
        while ((this.role == null && !authorized()
                || this.role == Role.USER && authorized())
                || this.role == Role.ADMIN && isAdmin()) {
            try {
                System.out.println();
                System.out.println(title);
                items.entrySet().stream()
                        .filter(e -> e.getValue().getRole() == Role.USER
                                || e.getValue().getRole() == Role.ADMIN && isAdmin())
                        .forEach(e -> System.out.printf("%d. %s%n", e.getKey(), e.getValue().getTitle()));

                System.out.print("Выберите пункт меню: ");

                if (scanner.hasNextInt()) {
                    String input = scanner.nextLine();
                    MenuItem item = items.get(Integer.parseInt(input));
                    if (item != null) {
                        System.out.println();
                        item.execute();
                    } else {
                        throw new IllegalArgumentException("Неверный пункт меню"); //TODO custom exception
                    }
                } else {
                    scanner.nextLine();
                    throw new IllegalArgumentException("Неверный пункт меню 2"); //TODO custom exception
                }
            } catch (RuntimeException e) {
                System.out.println(MessageColor.RED.colored(e.getMessage()));
            }
        }
    }

    private boolean authorized() {
        return PersonalMoneyTracker.getCurrentUser() != null;
    }

    private boolean isAdmin() {
        return PersonalMoneyTracker.getCurrentUser() != null
                && PersonalMoneyTracker.getCurrentUser().isAdmin();
    }
}
