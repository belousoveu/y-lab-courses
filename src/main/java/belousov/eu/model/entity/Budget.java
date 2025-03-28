package belousov.eu.model.entity;


import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Класс, представляющий бюджет пользователя на определённый период.
 * Содержит информацию о бюджете, такую как идентификатор, период, категория, пользователь и сумма.
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Budget {
    /**
     * Идентификатор бюджета.
     */
    @Getter
    @Setter
    private int id;
    /**
     * Период бюджета.
     */
    private LocalDate period;
    /**
     * Категория бюджета.
     */
    @Getter
    @Setter
    private Category category;
    /**
     * Пользователь, который устанавливает бюджет.
     */
    @Getter
    @Setter
    private User user;
    /**
     * Сумма бюджета.
     */
    @Getter
    @Setter
    private double amount;


    public YearMonth getPeriod() {
        return YearMonth.from(period);
    }

    public void setPeriod(YearMonth period) {
        this.period = period.atDay(1);
    }


}
