package belousov.eu.model.dto;

import belousov.eu.model.Category;
import belousov.eu.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий отчёт по выполнению бюджета.
 * Содержит информацию о периоде, пользователе, строках отчёта и итоговой строке.
 */
@Setter
@Getter
public class BudgetReport {
    /**
     * Период отчёта.
     */
    private YearMonth period;
    /**
     * Пользователь, для которого формируется отчёт.
     */
    private User user;
    /**
     * Список строк отчёта.
     */
    private final List<ReportRow> reportRows = new ArrayList<>();
    /**
     * Итоговая строка отчёта.
     */
    private final TotalRow totalRow = new TotalRow();

    /**
     * Добавляет строку отчёта с указанными категорией, суммой и фактическим расходом.
     *
     * @param category категория
     * @param amount   плановая сумма
     * @param spent    фактический расход
     */
    public void addReportRow(Category category, double amount, double spent) {
        reportRows.add(new ReportRow(category, amount, spent));
        totalRow.amount += amount;
        totalRow.spent += spent;
    }

    /**
     * Возвращает строку итоговой строки отчёта.
     *
     * @return строка итоговой строки отчёта
     */
    public String getTotalRow() {
        return totalRow.toString();
    }

    /**
     * Внутренний класс, представляющий строку отчёта.
     * Содержит информацию о категории, плановой сумме, фактическом расходе и проценте использования.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    private static class ReportRow {
        /**
         * Категория.
         */
        private Category category;
        /**
         * Плановая сумма.
         */
        private double amount;
        /**
         * Фактический расход.
         */
        private double spent;

        /**
         * Возвращает строку с информацией о строке отчёта.
         *
         * @return строка с информацией о строке отчёта
         */
        @Override
        public String toString() {
            return "Категория: %s, План: %,.2f, Факт: %,.2f, Использовано: %,.2f%%"
                    .formatted(category.getName(), amount, spent, spent * 100.0 / amount);
        }
    }

    /**
     * Внутренний класс, представляющий итоговую строку отчёта.
     * Содержит информацию о плановой сумме, фактическом расходе и проценте использования.
     */
    @Getter
    @Setter
    private static class TotalRow {
        /**
         * Итоговая плановая сумма.
         */
        private double amount;
        /**
         * Итоговый фактический расход.
         */
        private double spent;

        /**
         * Возвращает строку с информацией об итоговой строке отчёта.
         *
         * @return строка с информацией об итоговой строке отчёта
         */
        @Override
        public String toString() {
            return "ИТОГО: План: %,.2f, Факт: %,.2f, Использовано: %,.2f%%"
                    .formatted(amount, spent, spent * 100.0 / amount);
        }
    }
}
