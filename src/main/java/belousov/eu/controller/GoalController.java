package belousov.eu.controller;

import belousov.eu.model.Transaction;
import belousov.eu.observer.BalanceChangeObserver;
import belousov.eu.service.GoalService;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GoalController implements BalanceChangeObserver {

    private final GoalService goalService;
    private final ConsoleView consoleView;

    public void addGoal() {
        String name = consoleView.readString("Введите название цели:", InputPattern.NAME);
        String description = consoleView.readString("Описание цели:");
        Double point = consoleView.readDouble("Введите сумму цели:", InputPattern.SUM);
        goalService.addGoal(name, description, point);
        consoleView.println(String.format("Цель %s успешно добавлена", name), MessageColor.CYAN);
    }

    public void deleteGoal() {
        int goalId = consoleView.readInt("Введите id цели:", InputPattern.POSITIVE_INTEGER);
        goalService.deleteGoal(goalId);
        consoleView.println(String.format("Цель с id %d успешно удалена", goalId), MessageColor.CYAN);
    }

    public void editGoal() {
        int goalId = consoleView.readInt("Введите id цели:", InputPattern.POSITIVE_INTEGER);
        String name = consoleView.readString("Введите новое название цели:", InputPattern.NAME);
        String description = consoleView.readString("Введите новое описание цели:");
        Double point = consoleView.readDouble("Введите новую сумму цели:", InputPattern.SUM);
        goalService.editGoal(goalId, name, description, point);
        consoleView.println(String.format("Цель с id %d успешно отредактирована", goalId), MessageColor.CYAN);
    }

    public void viewGoals() {

        consoleView.println("Ваши цели:", goalService.getAll(), MessageColor.WHITE, MessageColor.YELLOW);
    }

    @Override
    public void balanceChanged(Transaction lastTransaction) {
        List<String> checkedGoal = goalService.checkGoal(lastTransaction);
        if (!checkedGoal.isEmpty()) {
            consoleView.println("Поздравляем! Есть достижения:", checkedGoal, MessageColor.GREEN, MessageColor.GREEN);
        }

    }
}
