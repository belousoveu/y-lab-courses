package belousov.eu.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Класс, представляющий бюджет пользователя на определённый период.
 * Содержит информацию о бюджете, такую как идентификатор, период, категория, пользователь и сумма.
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "budgets", schema = "app")
public class Budget {
    /**
     * Идентификатор бюджета.
     */
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "budget_id_seq_generator")
    @SequenceGenerator(name = "budget_id_seq_generator", schema = "app", sequenceName = "budget_id_seq", allocationSize = 1)
    private int id;
    /**
     * Период бюджета.
     */

    private LocalDate period;
    /**
     * Категория бюджета.
     */
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @Getter
    @Setter
    private Category category;
    /**
     * Пользователь, который устанавливает бюджет.
     */
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
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
