package belousov.eu.model.dto;

import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.OperationType;
import belousov.eu.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Класс, представляющий фильтр для транзакций.
 * Содержит критерии для фильтрации транзакций, такие как пользователь, период, категория и тип операции.
 */
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransactionFilter {

    /**
     * Пользователь, по которому выполняется фильтрация.
     */
    private User user;
    /**
     * Дата начала периода, по которому выполняется фильтрация.
     */
    private LocalDate from;
    /**
     * Дата конца периода, по которому выполняется фильтрация.
     */
    private LocalDate to;
    /**
     * Категория, по которой выполняется фильтрация.
     */
    private Category category;
    /**
     * Тип операции, по которому выполняется фильтрация.
     */
    private OperationType type;

}
