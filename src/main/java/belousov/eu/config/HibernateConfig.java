package belousov.eu.config;

import belousov.eu.exception.HibernateConfigException;
import belousov.eu.model.entity.*;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.Map;

@Getter
public class HibernateConfig {

    private final SessionFactory sessionFactory;


    public HibernateConfig(Map<String, Object> properties) {

        try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(properties).build()) {
            Configuration configuration = new Configuration()
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Category.class)
                    .addAnnotatedClass(Goal.class)
                    .addAnnotatedClass(Budget.class)
                    .addAnnotatedClass(Transaction.class);

            this.sessionFactory = configuration.buildSessionFactory(registry);
        } catch (Exception e) {
            throw new HibernateConfigException(e);
        }

    }

}





