package belousov.eu.repository;

import belousov.eu.model.entity.Goal;

import java.util.List;
import java.util.Optional;

public interface GoalRepository {
    Optional<Goal> findById(int id);

    List<Goal> findAllByUser(int userId);

    void delete(Goal goal);

    void save(Goal goal);
}
