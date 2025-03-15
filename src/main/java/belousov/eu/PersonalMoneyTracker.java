package belousov.eu;

import belousov.eu.config.ApplicationInitializer;
import belousov.eu.config.DependencyContainer;
import belousov.eu.config.MenuInitializer;
import belousov.eu.model.User;
import belousov.eu.view.Menu;
import lombok.Getter;
import lombok.Setter;

public class PersonalMoneyTracker {

    @Getter
    private static DependencyContainer container;
    @Getter
    @Setter
    private static User currentUser = null;
    @Getter
    @Setter
    private static boolean isRunning = true;


    public static void main(String[] args) {


        ApplicationInitializer app = ApplicationInitializer.initialize();
        container = app.getContainer();

        Menu registrationMenu = MenuInitializer.initializeLoginMenu(container);
        Menu mainMenu = MenuInitializer.initializeMainMenu(container);


        while (isRunning) {
            registrationMenu.display();
            mainMenu.display();
        }
    }

}