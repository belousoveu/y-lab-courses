package belousov.eu.config;

import belousov.eu.PersonalMoneyTracker;
import lombok.Getter;


@Getter
public class ApplicationInitializer {
    private final DependencyContainer container;
    private final ConfigLoader config;
    private static ApplicationInitializer instance;
    private static TomcatConfig tomcatConfig;


    private ApplicationInitializer(DependencyContainer container, ConfigLoader config) {
        this.container = container;
        this.config = config;
    }


    public static ApplicationInitializer initialize() {
        if (instance == null) {
            ConfigLoader config = new ConfigLoader();
            LiquibaseConfig.initialize(config.getConfig());
            HibernateConfig hibernateConfig = new HibernateConfig(config.getConfig());
            DependencyContainer container = new DependencyContainer(hibernateConfig);
            instance = new ApplicationInitializer(container, config);
            tomcatConfig = new TomcatConfig(container);
        }
        return instance;

    }

    public void start() {


        while (PersonalMoneyTracker.isRunning()) {
            tomcatConfig.start();
        }
    }

}
