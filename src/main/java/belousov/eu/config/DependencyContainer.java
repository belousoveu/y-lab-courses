package belousov.eu.config;

import belousov.eu.controller.*;
import belousov.eu.observer.BalanceChangeSubject;
import belousov.eu.repository.*;
import belousov.eu.service.*;
import belousov.eu.service.imp.*;
import belousov.eu.servlet.*;
import belousov.eu.view.ConsoleView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {

    private final Map<Class<?>, Object> dependencies = new HashMap<>();

    public DependencyContainer(HibernateConfig hibernateConfig) {

        register(SessionFactory.class, hibernateConfig.getSessionFactory());
        register(ObjectMapper.class, new ObjectMapper());

        //Repositories
        register(UserRepository.class,
                new UserRepository(
                        this.get(SessionFactory.class)
                ));
        register(GoalRepository.class,
                new GoalRepository(
                        this.get(SessionFactory.class)
                ));
        register(CategoryRepository.class,
                new CategoryRepository(
                        this.get(SessionFactory.class)
                ));
        register(BudgetRepository.class,
                new BudgetRepository(
                        this.get(SessionFactory.class)
                ));
        register(TransactionRepository.class,
                new TransactionRepository(
                        this.get(SessionFactory.class)
                ));

        register(BalanceChangeSubject.class, new BalanceChangeSubject());
        register(ConsoleView.class, new ConsoleView());

        //Services
        register(EmailService.class,
                new MockEmailServiceImp()
        );
        register(UserService.class,
                new UserService(
                        this.get(UserRepository.class)
                ));
        register(TransactionServiceImp.class,
                new TransactionServiceImp(
                        this.get(TransactionRepository.class),
                        this.get(BalanceChangeSubject.class)
                ));
        register(ReportService.class,
                this.get(TransactionServiceImp.class)
        );
        register(TransactionService.class,
                this.get(TransactionServiceImp.class)
        );

        register(CategoryService.class,
                new CategoryServiceImp(
                        this.get(CategoryRepository.class)
                ));
        register(GoalService.class,
                new GoalServiceImp(
                        this.get(ReportService.class),
                        this.get(EmailService.class),
                        this.get(GoalRepository.class)
                ));
        register(BudgetService.class,
                new BudgetServiceImp(
                        this.get(TransactionService.class),
                        this.get(CategoryService.class),
                        this.get(EmailService.class),
                        this.get(BudgetRepository.class)
                ));
        register(AdminAccessTransactionService.class,
                get(TransactionServiceImp.class)
        );
        register(AuthService.class,
                this.get(UserService.class)
        );
        register(ProfileService.class,
                this.get(UserService.class)
        );
        register(AdminAccessUserService.class,
                this.get(UserService.class)
        );
        register(AdminService.class,
                new AdminServiceImp(
                        this.get(AdminAccessUserService.class),
                        this.get(AdminAccessTransactionService.class)
                ));

        //Controllers
        register(TransactionController.class,
                new TransactionController(
                        this.get(TransactionService.class),
                        this.get(CategoryService.class),
                        this.get(ConsoleView.class)
                ));
        register(BudgetController.class,
                new BudgetController(
                        this.get(BudgetService.class),
                        this.get(ConsoleView.class)
                ));
        register(CategoryController.class,
                new CategoryController(
                        this.get(CategoryService.class)
                ));
        register(AuthController.class,
                new AuthController(
                        this.get(AuthService.class)
                ));
        register(ProfileController.class,
                new ProfileController(
                        this.get(ProfileService.class)
                ));
        register(AdminController.class,
                new AdminController(
                        this.get(AdminService.class)
                ));
        register(GoalController.class,
                new GoalController(
                        this.get(GoalService.class),
                        this.get(ConsoleView.class)
                ));
        register(ReportController.class,
                new ReportController(this.get(ReportService.class),
                        this.get(ConsoleView.class)
                ));

        register(AuthServlet.class,
                new AuthServlet(
                        this.get(AuthController.class),
                        this.get(ObjectMapper.class)
                ));
        register(ProfileServlet.class,
                new ProfileServlet(
                        this.get(ProfileController.class),
                        this.get(ObjectMapper.class)
                ));
        register(AdminServlet.class,
                new AdminServlet(
                        this.get(AdminController.class),
                        this.get(ObjectMapper.class)
                ));
        register(GoalServlet.class,
                new GoalServlet(
                        this.get(GoalController.class),
                        this.get(ObjectMapper.class)
                ));
        register(CategoryServlet.class,
                new CategoryServlet(
                        this.get(CategoryController.class),
                        this.get(ObjectMapper.class)
                ));
        register(BudgetServlet.class,
                new BudgetServlet(
                        this.get(BudgetController.class),
                        this.get(ObjectMapper.class)
                ));

        BalanceChangeSubject subject = this.get(BalanceChangeSubject.class);
        subject.addObserver(this.get(BudgetController.class));
        subject.addObserver(this.get(GoalController.class));

    }

    public <T> void register(Class<T> clazz, T dependency) {
        dependencies.put(clazz, dependency);
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(dependencies.get(clazz));
    }

}
