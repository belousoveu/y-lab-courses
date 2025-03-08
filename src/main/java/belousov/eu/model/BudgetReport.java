package belousov.eu.model;

import java.time.YearMonth;

public class BudgetReport {
    private int id;
    private YearMonth period;
    private Category category;
    private User user;
    private int amount;
    private int spent;
}
