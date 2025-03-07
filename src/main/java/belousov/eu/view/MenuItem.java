package belousov.eu.view;

import belousov.eu.controller.command.CommandMenu;
import lombok.Getter;

public class MenuItem {
    @Getter
    private final String title;
    private final CommandMenu command;


    public MenuItem(String title, CommandMenu command) {
        this.title = title;
        this.command = command;
    }

    public void execute() {
        command.execute();
    }

}
