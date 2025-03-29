package belousov.eu.controller;

import belousov.eu.model.dto.GoalDto;
import belousov.eu.model.entity.User;
import belousov.eu.service.GoalService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void addGoal(User user, GoalDto goalDto) {
        goalService.addGoal(user, goalDto);
    }

    public void deleteGoal(int goalId, User user) {
        goalService.deleteGoal(goalId, user);
    }

    public void editGoal(int goalId, User user, GoalDto goalDto) {
        goalService.editGoal(goalId, user, goalDto);
    }

    public List<GoalDto> getAllGoals(int userId) {
        return goalService.getAllByUserId(userId);

    }

}
