package belousov.eu;

import belousov.eu.config.ApplicationConfig;
import belousov.eu.config.DemoDataInitializer;
import belousov.eu.config.DependencyContainer;
import belousov.eu.config.MenuInitializer;
import belousov.eu.model.User;
import belousov.eu.view.Menu;
import lombok.Getter;
import lombok.Setter;

public class PersonalMoneyTracker {

    @Getter
    private static final DependencyContainer container = new DependencyContainer();
    @Getter
    @Setter
    private static User currentUser;
    @Getter
    @Setter
    private static boolean isRunning = true;


    public static void main(String[] args) {

        ApplicationConfig config = new ApplicationConfig();


        boolean idDemoMode = args.length > 0 && args[0].equals("-demo");

        if (idDemoMode) {
            DemoDataInitializer.initialize(container).createDemoData();
        }

        Menu registrationMenu = MenuInitializer.initializeLoginMenu(container);
        Menu mainMenu = MenuInitializer.initializeMainMenu(container);


        while (isRunning) {
            registrationMenu.display();
            mainMenu.display();
        }
    }

}