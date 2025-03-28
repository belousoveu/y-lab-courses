package belousov.eu.service;

import belousov.eu.model.dto.GoalDto;
import belousov.eu.model.entity.Transaction;
import belousov.eu.model.entity.User;

import java.util.List;

public interface GoalService {

    void addGoal(User user, GoalDto dto);

    void deleteGoal(int goalId, User user);

    void editGoal(int goalId, User user, GoalDto dto);

    List<GoalDto> getAllByUserId(int userId);

    List<String> checkGoal(Transaction lastTransaction);
}
