package belousov.eu.repository;

import belousov.eu.model.Budget;
import belousov.eu.model.Category;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тестовый класс для {@link BudgetRepository}.
 */
class BudgetRepositoryTest {

    private BudgetRepository budgetRepository;
    private User user;
    private Category category1;
    private Category category2;
    private YearMonth period;

    @BeforeEach
    void setUp() {
        budgetRepository = new BudgetRepository();
        user = new User(1, "John Doe", "john@example.com", "password123", Role.USER, true);
        category1 = new Category(1, "Продукты", user);
        category2 = new Category(2, "Транспорт", user);
        period = YearMonth.of(2023, 10);
    }

    @Test
    void test_save_whenNewBudget_shouldAddBudgetAndGenerateId() {
        Budget budget = new Budget(0, period, category1, user, 10000.0);
        budgetRepository.save(budget);

        assertThat(budget.getId()).isNotZero();
        assertThat(budgetRepository.findByCategoryAndPeriod(category1, user, period)).contains(budget);
    }

    @Test
    void test_save_whenExistingBudget_shouldUpdateBudget() {
        Budget budget = new Budget(0, period, category1, user, 10000.0);
        budgetRepository.save(budget);

        budget.setAmount(15000.0);
        budgetRepository.save(budget);

        Optional<Budget> updatedBudget = budgetRepository.findByCategoryAndPeriod(category1, user, period);
        assertThat(updatedBudget).isPresent();
        assertThat(updatedBudget.get().getAmount()).isEqualTo(15000.0);
    }

    @Test
    void test_findAllByPeriod_shouldReturnBudgetsForUserAndPeriod() {
        Budget budget1 = new Budget(0, period, category1, user, 10000.0);
        Budget budget2 = new Budget(0, period, category2, user, 5000.0);
        budgetRepository.save(budget1);
        budgetRepository.save(budget2);

        List<Budget> budgets = budgetRepository.findAllByPeriod(user, period);
        assertThat(budgets).containsExactlyInAnyOrder(budget1, budget2);
    }

    @Test
    void test_findAllByPeriod_whenNoBudgetsExist_shouldReturnEmptyList() {
        List<Budget> budgets = budgetRepository.findAllByPeriod(user, period);
        assertThat(budgets).isEmpty();
    }

    @Test
    void test_findByCategoryAndPeriod_whenBudgetExists_shouldReturnBudget() {
        Budget budget = new Budget(0, period, category1, user, 10000.0);
        budgetRepository.save(budget);

        Optional<Budget> foundBudget = budgetRepository.findByCategoryAndPeriod(category1, user, period);
        assertThat(foundBudget).contains(budget);
    }

    @Test
    void test_findByCategoryAndPeriod_whenBudgetDoesNotExist_shouldReturnEmpty() {
        Optional<Budget> foundBudget = budgetRepository.findByCategoryAndPeriod(category1, user, period);
        assertThat(foundBudget).isEmpty();
    }
}