package belousov.eu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BudgetReport {
    private YearMonth period;
    private User user;
    private final List<ReportRow> reportRows = new ArrayList<>();
    private final TotalRow totalRow = new TotalRow();


    public void addReportRow(Category category, double amount, double spent) {
        reportRows.add(new ReportRow(category, amount, spent));
        totalRow.amount += amount;
        totalRow.spent += spent;
    }

    public String getTotalRow() {
        return totalRow.toString();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class ReportRow {
        private Category category;
        private double amount;
        private double spent;

        @Override
        public String toString() {
            return "Категория: %s, План: %,.2f, Факт: %,.2f, Использовано: %,.2f%%"
                    .formatted(category.getName(), amount, spent, spent * 100.0 / amount);
        }
    }

    @Getter
    @Setter
    private static class TotalRow {
        private double amount;
        private double spent;

        @Override
        public String toString() {
            return "ИТОГО: План: %,.2f, Факт: %,.2f, Использовано: %,.2f%%"
                    .formatted(amount, spent, spent * 100.0 / amount);
        }
    }
}
