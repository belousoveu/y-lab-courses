package belousov.eu.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({DataSourceConfig.class, LiquibaseConfig.class})
@EnableAspectJAutoProxy
@EnableTransactionManagement
@ComponentScan(basePackages = {"belousov.eu.service", "belousov.eu.repository"})
public class AppConfig {
}