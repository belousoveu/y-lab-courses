package belousov.eu.repository;

import belousov.eu.model.Budget;
import belousov.eu.model.Category;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;
import lombok.AllArgsConstructor;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для управления бюджетами пользователей.
 * Обеспечивает хранение, добавление и поиск бюджетов по категориям и периодам.
 */
@AllArgsConstructor
public class BudgetRepository {

    private final Map<Integer, Budget> budgetMap = new HashMap<>();
    private final IdGenerator<Integer> idGenerator = IdGenerator.create(Integer.class);

    /**
     * Сохраняет бюджет в репозитории. Если бюджет новый (ID = 0), генерирует для него ID.
     *
     * @param budget бюджет для сохранения
     */
    public void save(Budget budget) {
        if (budget.getId() == 0) {
            budget.setId(idGenerator.nextId());
        }
        budgetMap.put(budget.getId(), budget);
    }

    /**
     * Возвращает список всех бюджетов для указанного пользователя и периода.
     *
     * @param currentUser пользователь, чьи бюджеты нужно найти
     * @param period      период (год и месяц)
     * @return список бюджетов пользователя за указанный период
     */
    public List<Budget> findAllByPeriod(User currentUser, YearMonth period) {
        return budgetMap.values().stream()
                .filter(budget -> budget.getUser().equals(currentUser) && budget.getPeriod().equals(period))
                .toList();
    }

    /**
     * Находит бюджет по категории, пользователю и периоду.
     *
     * @param category категория бюджета
     * @param user     пользователь
     * @param period   период (год и месяц)
     * @return Optional с бюджетом, если найден, иначе пустой Optional
     */
    public Optional<Budget> findByCategoryAndPeriod(Category category, User user, YearMonth period) {
        return budgetMap.values().stream()
                .filter(budget -> budget.getCategory().equals(category) && budget.getUser().equals(user) && budget.getPeriod().equals(period))
                .findFirst();
    }

}
