package belousov.eu.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

/**
 * Класс, представляющий бюджет пользователя на определённый период.
 * Содержит информацию о бюджете, такую как идентификатор, период, категория, пользователь и сумма.
 */
@AllArgsConstructor
@Getter
@Setter
public class Budget {
    /**
     * Идентификатор бюджета.
     */
    private int id;
    /** Период бюджета. */
    private YearMonth period;
    /** Категория бюджета. */
    private Category category;
    /** Пользователь, который устанавливает бюджет. */
    private User user;
    /** Сумма бюджета. */
    private double amount;
}
