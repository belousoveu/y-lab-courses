package belousov.eu.view;

import belousov.eu.controller.command.CommandMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {
    private final String title;
    private final Map<Integer, MenuItem> items = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    public Menu(String title) {
        this.title = title;
    }


    public void add(int key, String title, CommandMenu command) {
        items.put(key, new MenuItem(title, command));
    }

    public void display() {
        while (true) {
            System.out.println(title);
            items.forEach((key, value) -> System.out.printf("%d. %s%n", key, value.getTitle()));

            System.out.print("Выберите пункт меню: ");

            if (scanner.hasNextInt()) {
                String input = scanner.nextLine();

                MenuItem item = items.get(Integer.parseInt(input));
                if (item != null) {
                    item.execute();
                } else {
                    System.out.println("Неверный пункт меню");
                }

            } else {
                scanner.nextLine();
                System.out.println("Неверный пункт меню");
            }
        }
    }
}
