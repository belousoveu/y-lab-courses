package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.ProfileController;

public class ChangeUsernameCommand implements CommandMenu {

    private final ProfileController controller;

    public ChangeUsernameCommand(DependencyContainer container) {
        controller = container.get(ProfileController.class);
    }

    @Override
    public void execute() {
        controller.changeName();
    }
}
