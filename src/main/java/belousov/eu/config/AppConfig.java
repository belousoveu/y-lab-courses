package belousov.eu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({DataSourceConfig.class, WebConfig.class})
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class AppConfig {
}