package belousov.eu.controller;

import belousov.eu.model.dto.GoalDto;
import belousov.eu.model.entity.Transaction;
import belousov.eu.model.entity.User;
import belousov.eu.observer.BalanceChangeObserver;
import belousov.eu.service.GoalService;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GoalController implements BalanceChangeObserver {

    private final GoalService goalService;
    private final ConsoleView consoleView;

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

    @Override
    public void balanceChanged(Transaction lastTransaction) {
        List<String> checkedGoal = goalService.checkGoal(lastTransaction);
        if (!checkedGoal.isEmpty()) {
            consoleView.println("Поздравляем! Есть достижения:", checkedGoal, MessageColor.GREEN, MessageColor.GREEN);
        }

    }
}
