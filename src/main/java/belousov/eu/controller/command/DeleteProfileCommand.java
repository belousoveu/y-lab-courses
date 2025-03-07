package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.ProfileController;

public class DeleteProfileCommand implements CommandMenu{

    private final ProfileController controller;

    public DeleteProfileCommand(DependencyContainer container) {
        controller = container.get(ProfileController.class);
    }

    @Override
    public void execute() {
        controller.deleteProfile();
    }
}
