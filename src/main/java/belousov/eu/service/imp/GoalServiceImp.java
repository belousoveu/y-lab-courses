package belousov.eu.service.imp;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.GoalNotFoundException;
import belousov.eu.mapper.GoalMapper;
import belousov.eu.model.Goal;
import belousov.eu.model.Transaction;
import belousov.eu.model.User;
import belousov.eu.model.dto.GoalDto;
import belousov.eu.repository.GoalRepository;
import belousov.eu.service.EmailService;
import belousov.eu.service.GoalService;
import belousov.eu.service.ReportService;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса для управления финансовыми целями.
 * Обеспечивает добавление, удаление, редактирование и проверку достижения целей.
 */
@AllArgsConstructor
public class GoalServiceImp implements GoalService {
    /**
     * Сервис для создания отчетов.
     */
    private final ReportService reportService;
    /**
     * Сервис для отправки электронных писем.
     */
    private final EmailService emailService;
    /**
     * Репозиторий для работы с финансовыми целями.
     */
    private final GoalRepository goalRepository;

    private final GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);
    /**
     * Добавляет новую финансовую цель.
     *
     * @param user    текущий пользователь
     * @param goalDto объект с данными о цели
     */
    @Override
    public void addGoal(User user, GoalDto goalDto) {
        Goal goal = goalMapper.toEntity(goalDto);
        goal.setId(0);
        goal.setUser(user);
        goalRepository.save(goal);
    }

    /**
     * Удаляет финансовую цель по ID.
     *
     * @param id ID цели
     * @param user    текущий пользователь
     *
     * @throws GoalNotFoundException если цель не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void deleteGoal(int id, User user) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new GoalNotFoundException(id));
        checkIfGoalBelongsToUser(goal, user);
        goalRepository.delete(goal);
    }

    /**
     * Редактирует финансовую цель по ID.
     *
     * @param id          ID цели
     * @param user        текущий пользователь
     * @param goalDto     объект с обновленными данными о цели
     *
     * @throws GoalNotFoundException если цель не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void editGoal(int id, User user, GoalDto goalDto) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new GoalNotFoundException(id));
        checkIfGoalBelongsToUser(goal, user);
        goal.setName(goalDto.name());
        goal.setDescription(goalDto.description());
        goal.setPoint(goalDto.point());
        goalRepository.save(goal);
    }

    /**
     * Возвращает список всех финансовых целей текущего пользователя.
     *
     * @return список всех финансовых целей текущего пользователя
     */
    @Override
    public List<GoalDto> getAllByUserId(int userId) {
        return goalRepository.findAllByUser(userId).stream().map(goalMapper::toDto).toList();
    }

    /**
     * Проверяет, достигнуты ли финансовые цели после последней транзакции.
     * Если достигнута хотя бы одна цель, отправляет электронное письмо с информацией о достигнутых целях.
     *
     * @param lastTransaction последняя транзакция
     * @return список достигнутых целей в виде строк
     */
    @Override
    public List<String> checkGoal(Transaction lastTransaction) {
        double balance = reportService.getCurrentBalance(lastTransaction.getUser()).amount();
        List<Goal> goals = goalRepository.findAllByUser(PersonalMoneyTracker.getCurrentUser().getId());
        List<String> result = new ArrayList<>();
        for (Goal goal : goals) {
            if (goal.getPoint() <= balance) {
                result.add("Цель: %s - %,.2f".formatted(goal.getName(), goal.getPoint()));
            }
        }
        if (!result.isEmpty()) {
            emailService.sendEmail(lastTransaction.getUser().getEmail(),
                    "У Вас достигнута цель",
                    String.join("\n", result));
        }
        return result;

    }

    /**
     * Проверяет, принадлежит ли цель текущему пользователю.
     *
     * @param goal цель для проверки
     * @param user текущий пользователь
     *
     * @throws GoalNotFoundException если цель не найдена или не принадлежит текущему пользователю
     */
    private void checkIfGoalBelongsToUser(Goal goal, User user) {
        if (!goal.getUser().equals(user)) {
            throw new GoalNotFoundException(goal.getId());
        }
    }

}
