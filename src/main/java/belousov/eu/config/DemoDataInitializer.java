package belousov.eu.config;

import belousov.eu.model.*;
import belousov.eu.repository.*;
import belousov.eu.utils.MessageColor;
import belousov.eu.utils.Password;
import belousov.eu.view.ConsoleView;

import java.time.LocalDate;
import java.time.YearMonth;

public class DemoDataInitializer {
    private final DependencyContainer container;

    private DemoDataInitializer(DependencyContainer container) {
        this.container = container;
    }

    public static DemoDataInitializer initialize(DependencyContainer container) {
        return new DemoDataInitializer(container);
    }

    public void createDemoData() {

        User userIvan = new User("Ivan", "ivan@gmail.com", Password.encode("Ivan0123"));
        User userPetr = new User("Petr", "petr@gmail.com", Password.encode("Petr0123"));

        UserRepository userRepository = container.get(UserRepository.class);

        userRepository.save(userIvan);
        userRepository.save(userPetr);

        CategoryRepository categoryRepository = container.get(CategoryRepository.class);

        Category categoryProductsIvan = new Category(0, "Продукты", userIvan);
        Category categoryBooksIvan = new Category(0, "Книги", userIvan);
        Category categoryProductsPetr = new Category(0, "Продукты", userPetr);
        Category categoryCreditsPetr = new Category(0, "Кредиты", userPetr);
        Category categoryGiftsPetr = new Category(0, "Подарки", userPetr);

        categoryRepository.save(categoryBooksIvan);
        categoryRepository.save(categoryProductsIvan);
        categoryRepository.save(categoryGiftsPetr);
        categoryRepository.save(categoryCreditsPetr);
        categoryRepository.save(categoryProductsPetr);

        GoalRepository goalRepository = container.get(GoalRepository.class);
        goalRepository.save(new Goal(0, userIvan, "Телефон", "Собрать деньги на новый телефон", 5000.0));

        BudgetRepository budgetRepository = container.get(BudgetRepository.class);
        budgetRepository.save(new Budget(0, YearMonth.of(2025, 1), categoryProductsIvan, userIvan, 1000));
        budgetRepository.save(new Budget(0, YearMonth.of(2025, 1), categoryBooksIvan, userIvan, 500));

        budgetRepository.save(new Budget(0, YearMonth.of(2025, 2), categoryProductsIvan, userIvan, 1200));
        budgetRepository.save(new Budget(0, YearMonth.of(2025, 2), categoryBooksIvan, userIvan, 1000));

        budgetRepository.save(new Budget(0, YearMonth.of(2025, 2), categoryProductsPetr, userPetr, 500));
        budgetRepository.save(new Budget(0, YearMonth.of(2025, 2), categoryCreditsPetr, userPetr, 1000));
        budgetRepository.save(new Budget(0, YearMonth.of(2025, 2), categoryGiftsPetr, userPetr, 500));


        TransactionRepository transactionRepository = container.get(TransactionRepository.class);

        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 1, 10), OperationType.DEPOSIT, null, 10000, "Заработная плата", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 1, 15), OperationType.WITHDRAW, categoryProductsIvan, 200.50, "Покупка продуктов", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 1, 15), OperationType.WITHDRAW, categoryBooksIvan, 50.00, "Покупка книг", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 1, 18), OperationType.WITHDRAW, categoryProductsIvan, 150.70, "Покупка продуктов", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 1, 25), OperationType.WITHDRAW, categoryBooksIvan, 200.00, "Покупка книг", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 1, 25), OperationType.WITHDRAW, categoryProductsIvan, 375.25, "Покупка продуктов", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 1, 30), OperationType.WITHDRAW, categoryProductsIvan, 200.00, "Покупка продуктов", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 5), OperationType.DEPOSIT, null, 10000, "Заработная плата", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 5), OperationType.WITHDRAW, categoryProductsIvan, 1200.00, "Покупка продуктов", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 6), OperationType.WITHDRAW, null, 500.00, "Поход в кино", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 12), OperationType.WITHDRAW, categoryProductsIvan, 700.00, "Покупка продуктов", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 15), OperationType.WITHDRAW, categoryBooksIvan, 500.00, "покупка книг", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 15), OperationType.DEPOSIT, null, 5000, "Выигрыш в лотерею", userPetr));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 16), OperationType.WITHDRAW, categoryProductsPetr, 454.80, "Покупка продуктов", userPetr));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 18), OperationType.WITHDRAW, categoryProductsIvan, 720.00, "Покупка продуктов", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 20), OperationType.WITHDRAW, categoryCreditsPetr, 1000.00, "Оплата кредита", userPetr));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 22), OperationType.WITHDRAW, categoryProductsPetr, 625.00, "Покупка продуктов", userPetr));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 25), OperationType.WITHDRAW, categoryBooksIvan, 800.00, "покупка книг", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 25), OperationType.WITHDRAW, categoryProductsIvan, 470.50, "Покупка продуктов", userIvan));
        transactionRepository.save(
                new Transaction(0, LocalDate.of(2025, 2, 28), OperationType.WITHDRAW, categoryGiftsPetr, 450.00, "Покупка подарков", userPetr));

        ConsoleView consoleView = container.get(ConsoleView.class);

        consoleView.println("Программа запущена в демо режиме", MessageColor.GREEN);
        consoleView.println("Для входа используйте следующие учетные данные:", MessageColor.GREEN);
        consoleView.println("Администратор: admin@admin.com/Admin123", MessageColor.YELLOW);
        consoleView.println("Пользователь: ivan@gmail.com/Ivan0123", MessageColor.YELLOW);
        consoleView.println("Пользователь: petr@gmail.com/Petr0123", MessageColor.YELLOW);

    }
}
