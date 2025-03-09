package belousov.eu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий категорию транзакций.
 * Содержит информацию о категории, такую как идентификатор, название и пользователь, которому она принадлежит.
 */
@AllArgsConstructor
@Getter
@Setter
public class Category {
    /**
     * Уникальный идентификатор категории.
     */
    private int id;
    /**
     * Название категории.
     */
    private String name;
    /**
     * Пользователь, которому принадлежит категория.
     */
    private User user;

    /**
     * Возвращает строковое представление категории.
     *
     * @return строка, содержащая информацию о категории
     */
    @Override
    public String toString() {
        return name;
    }
}
