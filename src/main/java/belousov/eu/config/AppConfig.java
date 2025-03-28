package belousov.eu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({DataSourceConfig.class, WebConfig.class})
@EnableTransactionManagement
public class AppConfig {
}