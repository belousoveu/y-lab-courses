package belousov.eu.repository;

import belousov.eu.model.Goal;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class GoalRepository {

    private final Map<Integer, Goal> goals = new HashMap<>();
    private final IdGenerator<Integer> idCounter = IdGenerator.create(Integer.class);


    public Optional<Goal> findById(int id) {
        return Optional.ofNullable(goals.get(id));
    }

    public List<Goal> findAllByUser(User currentUser) {
        return goals.values().stream().filter(goal -> goal.getUser().equals(currentUser)).toList();
    }

    public void delete(Goal goal) {
        goals.remove(goal.getId());
    }

    public void save(Goal goal) {
        if (goal.getId() == 0) {
            goal.setId(idCounter.nextId());
        }
        goals.put(goal.getId(), goal);
    }
}
