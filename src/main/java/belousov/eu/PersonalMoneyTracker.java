package belousov.eu;

import belousov.eu.config.ApplicationInitializer;
import belousov.eu.model.User;
import lombok.Getter;
import lombok.Setter;

public class PersonalMoneyTracker {

    @Getter
    @Setter
    private static User currentUser = null;
    @Getter
    @Setter
    private static boolean isRunning = true;


    public static void main(String[] args) {


        ApplicationInitializer app = ApplicationInitializer.initialize();

        app.start();

    }

}