package belousov.eu.service;

import belousov.eu.exception.GoalNotFoundException;
import belousov.eu.mapper.GoalMapper;
import belousov.eu.model.dto.BalanceDto;
import belousov.eu.model.dto.GoalDto;
import belousov.eu.model.entity.Goal;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.Transaction;
import belousov.eu.model.entity.User;
import belousov.eu.repository.imp.GoalRepositoryImp;
import belousov.eu.service.imp.GoalServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link GoalServiceImp}.
 */
class GoalServiceImpTest {

    @Mock
    private ReportService reportService;

    @Mock
    private EmailService emailService;

    @Mock
    private GoalRepositoryImp goalRepository;

    @InjectMocks
    private GoalServiceImp goalServiceImp;

    private User user;
    private Goal goal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, "John Doe", "john@example.com", "password123", Role.USER, true);
        goal = new Goal(1, user, "Новая машина", "Накопить на новую машину", 1000000.0);
    }

    @Test
    void test_addGoal_shouldSaveGoal() {
        goalServiceImp.addGoal(user, new GoalDto(0, null, "Новая машина", "Накопить на новую машину", 1000000.0));

        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void test_deleteGoal_whenGoalExistsAndBelongsToUser_shouldDeleteGoal() {
        when(goalRepository.findById(1)).thenReturn(Optional.of(goal));

        goalServiceImp.deleteGoal(1, user);

        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    void test_deleteGoal_whenGoalDoesNotExist_shouldThrowException() {
        when(goalRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalServiceImp.deleteGoal(1, user))
                .isInstanceOf(GoalNotFoundException.class)
                .hasMessage("Не найдена цель с идентификатором 1");
    }

    @Test
    void test_deleteGoal_whenGoalDoesNotBelongToUser_shouldThrowException() {
        User otherUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        Goal otherGoal = new Goal(1, otherUser, "Новая машина", "Накопить на новую машину", 1000000.0);
        when(goalRepository.findById(1)).thenReturn(Optional.of(otherGoal));

        assertThatThrownBy(() -> goalServiceImp.deleteGoal(1, user))
                .isInstanceOf(GoalNotFoundException.class)
                .hasMessage("Не найдена цель с идентификатором 1");
    }

    @Test
    void test_editGoal_whenGoalExistsAndBelongsToUser_shouldUpdateGoal() {
        when(goalRepository.findById(1)).thenReturn(Optional.of(goal));

        goalServiceImp.editGoal(1, user, new GoalDto(1, user, "Квартира", "Накопить на квартиру", 5000000.0));

        assertThat(goal.getName()).isEqualTo("Квартира");
        assertThat(goal.getDescription()).isEqualTo("Накопить на квартиру");
        assertThat(goal.getPoint()).isEqualTo(5000000.0);
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void test_editGoal_whenGoalDoesNotExist_shouldThrowException() {
        when(goalRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalServiceImp.editGoal(1, user, new GoalDto(1, user, "Квартира", "Накопить на квартиру", 5000000.0)))
                .isInstanceOf(GoalNotFoundException.class)
                .hasMessage("Не найдена цель с идентификатором 1");
    }

    @Test
    void test_editGoal_whenGoalDoesNotBelongToUser_shouldThrowException() {
        User otherUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        Goal otherGoal = new Goal(1, otherUser, "Новая машина", "Накопить на новую машину", 1000000.0);
        when(goalRepository.findById(1)).thenReturn(Optional.of(otherGoal));

        assertThatThrownBy(() -> goalServiceImp.editGoal(1, user, new GoalDto(1, user, "Квартира", "Накопить на квартиру", 5000000.0)))
                .isInstanceOf(GoalNotFoundException.class)
                .hasMessage("Не найдена цель с идентификатором 1");
    }

    @Test
    void test_getAll_shouldReturnGoalsForUser() {
        when(goalRepository.findAllByUser(user.getId())).thenReturn(List.of(goal));
        GoalMapper mapper = Mappers.getMapper(GoalMapper.class);

        List<GoalDto> goals = goalServiceImp.getAllByUserId(user.getId());
        assertThat(goals).containsExactly(mapper.toDto(goal));
    }

    @Test
    void test_checkGoal_whenGoalsAreAchieved_shouldSendEmailAndReturnMessages() {
        when(reportService.getCurrentBalance(user)).thenReturn(new BalanceDto("01.01.2025", user.getName(), 1500000.0));
        when(goalRepository.findAllByUser(user.getId())).thenReturn(List.of(goal));

        Transaction transaction = new Transaction(1, null, null, null, 0.0, null, user);
        List<String> result = goalServiceImp.checkGoal(transaction);

        assertThat(result).containsExactly("Цель: Новая машина - 1 000 000,00");
        verify(emailService, times(1)).sendEmail(user.getEmail(), "У Вас достигнута цель", "Цель: Новая машина - 1 000 000,00");
    }

    @Test
    void test_checkGoal_whenNoGoalsAreAchieved_shouldReturnEmptyList() {
        when(reportService.getCurrentBalance(user)).thenReturn(new BalanceDto("01.01.2025", user.getName(), 500000.0));
        when(goalRepository.findAllByUser(user.getId())).thenReturn(List.of(goal));

        Transaction transaction = new Transaction(1, null, null, null, 0.0, null, user);
        List<String> result = goalServiceImp.checkGoal(transaction);

        assertThat(result).isEmpty();
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}