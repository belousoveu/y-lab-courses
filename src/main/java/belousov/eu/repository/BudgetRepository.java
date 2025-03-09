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

@AllArgsConstructor
public class BudgetRepository {

    private final Map<Integer, Budget> budgetMap = new HashMap<>();
    private final IdGenerator<Integer> idGenerator = IdGenerator.create(Integer.class);

    public void save(Budget budget) {
        if (budget.getId() == 0) {
            budget.setId(idGenerator.nextId());
        }
        budgetMap.put(budget.getId(), budget);
    }

    public List<Budget> findAllByPeriod(User currentUser, YearMonth period) {
        return budgetMap.values().stream()
                .filter(budget -> budget.getUser().equals(currentUser) && budget.getPeriod().equals(period))
                .toList();
    }

    public Optional<Budget> findByCategoryAndPeriod(Category category, User user, YearMonth period) {
        return budgetMap.values().stream()
                .filter(budget -> budget.getCategory().equals(category) && budget.getUser().equals(user) && budget.getPeriod().equals(period))
                .findFirst();
    }

}
