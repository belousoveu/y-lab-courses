package belousov.eu;

import belousov.eu.config.ApplicationInitializer;
import lombok.Getter;
import lombok.Setter;

public class PersonalMoneyTracker {

    @Getter
    @Setter
    private static boolean isRunning = true;


    public static void main(String[] args) {


        ApplicationInitializer app = ApplicationInitializer.initialize();

        app.start();

    }

}