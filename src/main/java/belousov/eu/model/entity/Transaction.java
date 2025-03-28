package belousov.eu.model.entity;


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
public class Transaction {
    /**
     * Уникальный идентификатор транзакции.
     */
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
    private OperationType operationType;
    /**
     * Категория транзакции.
     */
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
