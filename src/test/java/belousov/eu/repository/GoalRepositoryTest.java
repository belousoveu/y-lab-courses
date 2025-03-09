package belousov.eu.repository;

import belousov.eu.model.Goal;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тестовый класс для {@link GoalRepository}.
 */
class GoalRepositoryTest {

    private GoalRepository goalRepository;
    private User user;

    @BeforeEach
    void setUp() {
        goalRepository = new GoalRepository();
        user = new User(1, "John Doe", "john@example.com", "password123", Role.USER, true);
    }

    @Test
    void test_save_whenNewGoal_shouldAddGoalAndGenerateId() {
        Goal goal = new Goal(0, user, "Накопить на машину", "Накопить 2 500 000 на машину", 2_500_000.0);
        goalRepository.save(goal);

        assertThat(goal.getId()).isNotZero();
        assertThat(goalRepository.findById(goal.getId())).contains(goal);
    }

    @Test
    void test_save_whenExistingGoal_shouldUpdateGoal() {
        Goal goal = new Goal(0, user, "Накопить на машину", "Накопить 2 500 000 на машину", 2_500_000.0);
        goalRepository.save(goal);

        goal.setPoint(3_000_000.0);
        goalRepository.save(goal);

        Optional<Goal> updatedGoal = goalRepository.findById(goal.getId());
        assertThat(updatedGoal).isPresent();
        assertThat(updatedGoal.get().getPoint()).isEqualTo(3_000_000.0);
    }

    @Test
    void test_delete_shouldRemoveGoal() {
        Goal goal = new Goal(0, user, "Накопить на машину", "Накопить 2 500 000 на машину", 2_500_000.0);
        goalRepository.save(goal);

        goalRepository.delete(goal);
        assertThat(goalRepository.findById(goal.getId())).isEmpty();
    }

    @Test
    void test_findById_whenGoalExists_shouldReturnGoal() {
        Goal goal = new Goal(0, user, "Накопить на машину", "Накопить 2 500 000 на машину", 2_500_000.0);
        goalRepository.save(goal);

        Optional<Goal> foundGoal = goalRepository.findById(goal.getId());
        assertThat(foundGoal).contains(goal);
    }

    @Test
    void test_findById_whenGoalDoesNotExist_shouldReturnEmpty() {
        Optional<Goal> foundGoal = goalRepository.findById(999);
        assertThat(foundGoal).isEmpty();
    }

    @Test
    void test_findAllByUser_shouldReturnGoalsForUser() {
        Goal goal1 = new Goal(0, user, "Накопить на машину", "Накопить 2 500 000 на машину", 2_500_000.0);
        Goal goal2 = new Goal(0, user, "Накопить на дом", "Накопить 10 000 000 на дом", 10_000_000.0);
        goalRepository.save(goal1);
        goalRepository.save(goal2);

        List<Goal> goals = goalRepository.findAllByUser(user);
        assertThat(goals).containsExactlyInAnyOrder(goal1, goal2);
    }

    @Test
    void test_findAllByUser_whenNoGoalsExist_shouldReturnEmptyList() {
        List<Goal> goals = goalRepository.findAllByUser(user);
        assertThat(goals).isEmpty();
    }
}