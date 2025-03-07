package belousov.eu.view;

import belousov.eu.controller.command.CommandMenu;
import belousov.eu.model.Role;
import lombok.Getter;

public class MenuItem {
    @Getter
    private final String title;
    private final CommandMenu command;
    @Getter
    private final Role role;


    public MenuItem(String title, CommandMenu command) {
        this.title = title;
        this.command = command;
        this.role = Role.USER;
    }

    public MenuItem(String title, CommandMenu command, Role role) {
        this.title = title;
        this.command = command;
        this.role = role;
    }

    public void execute() {
        command.execute();
    }

}
