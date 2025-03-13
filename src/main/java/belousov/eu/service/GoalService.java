package belousov.eu.service;

import belousov.eu.model.Goal;
import belousov.eu.model.Transaction;

import java.util.List;

public interface GoalService {

    void addGoal(String name, String description, Double point);

    void deleteGoal(int goalId);

    void editGoal(int goalId, String name, String description, Double point);

    List<Goal> getAll();

    List<String> checkGoal(Transaction lastTransaction);
}
