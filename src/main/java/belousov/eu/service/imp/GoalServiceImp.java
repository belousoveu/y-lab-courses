package belousov.eu.service.imp;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.GoalNotFoundException;
import belousov.eu.model.Goal;
import belousov.eu.model.Transaction;
import belousov.eu.repository.GoalRepository;
import belousov.eu.service.EmailService;
import belousov.eu.service.GoalService;
import belousov.eu.service.ReportService;
import lombok.AllArgsConstructor;

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

    /**
     * Добавляет новую финансовую цель.
     *
     * @param name        название цели
     * @param description описание цели
     * @param point       количество средств для достижения цели
     */
    @Override
    public void addGoal(String name, String description, Double point) {

        goalRepository.save(new Goal(0, PersonalMoneyTracker.getCurrentUser(), name, description, point));

    }

    /**
     * Удаляет финансовую цель по ID.
     *
     * @param id ID цели
     * @throws GoalNotFoundException если цель не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void deleteGoal(int id) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new GoalNotFoundException(id));
        checkIfGoalBelongsToUser(goal);
        goalRepository.delete(goal);
    }

    /**
     * Редактирует финансовую цель по ID.
     *
     * @param id          ID цели
     * @param name        новое название цели
     * @param description новое описание цели
     * @param point       новое количество средств для достижения цели
     * @throws GoalNotFoundException если цель не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void editGoal(int id, String name, String description, Double point) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new GoalNotFoundException(id));
        checkIfGoalBelongsToUser(goal);
        goal.setName(name);
        goal.setDescription(description);
        goal.setPoint(point);
        goalRepository.save(goal);
    }

    /**
     * Возвращает список всех финансовых целей текущего пользователя.
     *
     * @return список всех финансовых целей текущего пользователя
     */
    @Override
    public List<Goal> getAll() {
        return goalRepository.findAllByUser(PersonalMoneyTracker.getCurrentUser());
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
        double balance = reportService.getCurrentBalance();
        List<Goal> goals = getAll();
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
     * @throws GoalNotFoundException если цель не найдена или не принадлежит текущему пользователю
     */
    private void checkIfGoalBelongsToUser(Goal goal) {
        if (!goal.getUser().equals(PersonalMoneyTracker.getCurrentUser())) {
            throw new GoalNotFoundException(goal.getId());
        }
    }

}
