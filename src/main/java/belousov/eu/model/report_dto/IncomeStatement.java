package belousov.eu.model.report_dto;

import belousov.eu.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Класс, представляющий отчёт о доходах и расходах за определённый период.
 * Содержит информацию о пользователе, периоде, доходах, расходах и итоговом балансе.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class IncomeStatement {
    /**
     * Пользователь, для которого составлен отчёт.
     */
    private User user;
    /**
     * Начальная дата периода, за который составлен отчёт.
     */
    private LocalDate from;
    /**
     * Конечная дата периода, за который составлен отчёт.
     */
    private LocalDate to;
    /**
     * Сумма доходов за указанный период.
     */
    private double income;
    /**
     * Сумма расходов за указанный период.
     */
    private double outcome;

    /**
     * Итог, разность доходов и расходов.
     */
    private double total;

    /**
     * Возвращает итоговый баланс, который равен разности доходов и расходов.
     *
     * @return итоговый баланс
     */
    @Override
    public String toString() {
        return """
                Отчет о доходах и расходах за период: %s - %s
                Доход: %,.2f
                Расход: %,.2f
                Итого: %,.2f
                """.formatted(from, to, income, outcome, income - outcome);

    }
}
