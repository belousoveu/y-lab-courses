package belousov.eu.config;

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


}
