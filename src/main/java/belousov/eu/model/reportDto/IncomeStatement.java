package belousov.eu.model.reportDto;

import belousov.eu.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class IncomeStatement {
    User user;
    LocalDate from;
    LocalDate to;
    double income;
    double outcome;

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
