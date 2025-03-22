package belousov.eu.service.imp;

import belousov.eu.mapper.BudgetMapper;
import belousov.eu.mapper.CategoryMapper;
import belousov.eu.model.*;
import belousov.eu.model.dto.BudgetDto;
import belousov.eu.model.dto.CategoryDto;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.report_dto.BudgetReport;
import belousov.eu.repository.BudgetRepository;
import belousov.eu.service.BudgetService;
import belousov.eu.service.CategoryService;
import belousov.eu.service.EmailService;
import belousov.eu.service.TransactionService;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления бюджетами.
 * Обеспечивает добавление бюджетов, формирование отчётов и проверку превышения бюджета.
 */
@AllArgsConstructor
public class BudgetServiceImp implements BudgetService {

    /**
     * Сервис для управления транзакциями.
     */
    private final TransactionService transactionService;
    /**
     * Сервис для работы с категориями.
     */
    private final CategoryService categoryService;
    /**
     * Сервис для отправки уведомлений по электронной почте.
     */
    private final EmailService emailService;

    /**
     * Репозиторий для управления бюджетами.
     */
    private final BudgetRepository budgetRepository;
    /**
     * Маппер для преобразования объектов бюджета в DTO и обратно.
     */
    private final BudgetMapper budgetMapper = Mappers.getMapper(BudgetMapper.class);
    /**
     * Маппер для преобразования объектов категории в DTO и обратно.
     */
    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    /**
     * Добавляет бюджеты для указанного периода и категории.
     *
     * @param user      текущий авторизованный пользователь
     * @param budgetDto объект с данными бюджета
     */
    @Override
    public void addBudget(User user, BudgetDto budgetDto) {
        budgetDto.setUserId(user.getId());
        CategoryDto categoryDto = categoryService.getCategory(budgetDto.getCategoryId(), user);
        Budget budget = budgetMapper.toEntity(budgetDto);
        budget.setCategory(categoryMapper.toEntity(categoryDto));
        budget.setUser(user);
        budgetRepository.save(budget);
    }

    /**
     * Подготавливает отчёт о бюджете для указанного периода.
     *
     * @param user   текущий авторизованный пользователь
     * @param period период
     * @return Возвращает объект BudgetReport, содержащий информацию о бюджете за указанный период.
     */
    @Override
    public BudgetReport getBudgetReport(User user, YearMonth period) {

        BudgetReport budgetReport = new BudgetReport();
        budgetReport.setPeriod(period);
        budgetReport.setUser(user);

        List<Budget> budgets = budgetRepository.findAllByPeriod(user, period);
        if (budgets.isEmpty()) {
            return budgetReport;
        }

        TransactionFilter filter = TransactionFilter.builder()
                .user(user)
                .from(period.atDay(1))
                .to(period.atEndOfMonth())
                .build();

        List<TransactionDto> transactions = transactionService.getTransactions(filter);


        for (Budget budget : budgets) {
            double spent = transactions.stream()
                    .filter(t -> t.category() != null)
                    .filter(t -> t.category().equals(budget.getCategory().getName())).mapToDouble(TransactionDto::amount).sum();
            budgetReport.addReportRow(budget.getCategory(), budget.getAmount(), spent);
        }

        return budgetReport;
    }

    /**
     * Проверяет, превышен ли бюджет для последней транзакции.
     *
     * @param lastTransaction последняя транзакция
     * @return сообщение о превышении бюджета или пустая строка, если бюджет не превышен
     */
    @Override
    public String checkBudget(Transaction lastTransaction) {
        if (lastTransaction.getOperationType() == OperationType.DEPOSIT || lastTransaction.getCategory() == null) {
            return "";
        }
        YearMonth period = YearMonth.of(lastTransaction.getDate().getYear(), lastTransaction.getDate().getMonth());
        Optional<Budget> budget = budgetRepository
                .findByCategoryAndPeriod(lastTransaction.getCategory(), lastTransaction.getUser(), period);

        if (budget.isPresent()) {
            List<TransactionDto> transactions = transactionService.getTransactions(TransactionFilter.builder()
                    .user(lastTransaction.getUser())
                    .category(lastTransaction.getCategory())
                    .from(period.atDay(1))
                    .to(period.atEndOfMonth())
                    .type(OperationType.WITHDRAW)
                    .build());
            double spent = transactions.stream().mapToDouble(TransactionDto::amount).sum();
            if (spent >= budget.get().getAmount()) {
                emailService.sendEmail(lastTransaction.getUser().getEmail(), "Бюджет превышен", "Превышен бюджет по категории %s на сумму %.2f");
                return "Превышен бюджет по категории %s на сумму %.2f"
                        .formatted(lastTransaction.getCategory().getName(), spent - budget.get().getAmount());
            }
        }
        return "";
    }

}
