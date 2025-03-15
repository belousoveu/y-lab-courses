package belousov.eu;

import belousov.eu.config.*;
import belousov.eu.model.User;
import belousov.eu.view.Menu;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.SessionFactory;

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
        LiquibaseConfig liquibaseConfig = new LiquibaseConfig(config.getConfig());
        liquibaseConfig.runMigration();
        SessionFactory sessionFactory = new HibernateConfig(config.getConfig()).getSessionFactory();


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