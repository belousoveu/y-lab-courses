package belousov.eu.service;

import belousov.eu.model.Goal;

import java.util.List;

public interface GoalService {

    void addGoal(String name, String description, Double point);

    void deleteGoal(int goalId);

    void editGoal(int goalId, String name, String description, Double point);

    List<Goal> getAll();
}
