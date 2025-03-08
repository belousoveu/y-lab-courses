package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.GoalNotFoundException;
import belousov.eu.model.Goal;
import belousov.eu.repository.GoalRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GoalServiceImp implements GoalService {

    private final GoalRepository goalRepository;

    @Override
    public void addGoal(String name, String description, Double point) {

        goalRepository.save(new Goal(0, PersonalMoneyTracker.getCurrentUser(), name, description, point));

    }

    @Override
    public void deleteGoal(int id) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new GoalNotFoundException(id));
        checkIfGoalBelongsToUser(goal);
        goalRepository.delete(goal);
    }

    @Override
    public void editGoal(int id, String name, String description, Double point) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new GoalNotFoundException(id));
        checkIfGoalBelongsToUser(goal);
        goal.setName(name);
        goal.setDescription(description);
        goal.setPoint(point);
        goalRepository.save(goal);

    }

    @Override
    public List<Goal> getAll() {
        return goalRepository.findAllByUser(PersonalMoneyTracker.getCurrentUser());
    }

    private void checkIfGoalBelongsToUser(Goal goal) {
        if (!goal.getUser().equals(PersonalMoneyTracker.getCurrentUser())) {
            throw new GoalNotFoundException(goal.getId());
        }
    }
}
