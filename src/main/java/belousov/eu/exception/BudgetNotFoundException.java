package belousov.eu.exception;

import java.time.YearMonth;

public class BudgetNotFoundException extends RuntimeException {

    public BudgetNotFoundException(YearMonth period) {
        super("Бюджет на период: %s не установлен".formatted(period));
    }
}
