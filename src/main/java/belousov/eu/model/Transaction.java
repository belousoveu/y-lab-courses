package belousov.eu.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Класс, представляющий транзакцию в системе.
 * Содержит информацию о транзакции, такую как идентификатор, дата, тип операции, категория, сумма, описание и пользователь.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "transactions", schema = "app")
public class Transaction {
    /**
     * Уникальный идентификатор транзакции.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_seq_generator")
    @SequenceGenerator(name = "transaction_id_seq_generator", schema = "app", sequenceName = "transaction_id_seq", allocationSize = 1)
    private int id;
    /**
     * Дата совершения транзакции.
     */
    private LocalDate date;
    /**
     * Тип операции
     * OperationType.DEPOSIT - поступление средств
     * OperationType.WITHDRAW - снятие средств
     */
    @Column(name = "operation_type")
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    /**
     * Категория транзакции.
     */
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    /**
     * Сумма транзакции.
     */
    private double amount;
    /**
     * Описание транзакции.
     */
    private String description;
    /**
     * Пользователь, связанный с транзакцией
     */
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Возвращает строковое представление транзакции без информации о пользователе.
     *
     * @return строка, содержащая информацию о транзакции
     */
    @Override
    public String toString() {
        return "id=%d, date=%s, operationType=%s, category=%s, amount=%,.2f, description=%s "
                .formatted(id, date, operationType, category == null ? "" : category.getName(), amount, description);
    }

    /**
     * Возвращает строковое представление транзакции с информацией о пользователе.
     *
     * @return строка, содержащая информацию о транзакции и пользователе
     */
    public String toStringWithUser() {
        return "id=%d, date=%s, operationType=%s, category=%s, amount=%,.2f, description=%s, user=%s"
                .formatted(id, date, operationType, category == null ? "" : category.getName(), amount, description, user.getName());
    }
}
