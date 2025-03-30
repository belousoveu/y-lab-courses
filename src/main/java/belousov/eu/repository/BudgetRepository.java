package belousov.eu.repository;

import belousov.eu.model.entity.Budget;
import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.User;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository {
    Budget save(Budget budget);

    List<Budget> findAllByPeriod(User currentUser, YearMonth period);

    Optional<Budget> findByCategoryAndPeriod(Category category, User user, YearMonth period);
}
