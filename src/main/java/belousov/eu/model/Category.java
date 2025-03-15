package belousov.eu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс, представляющий категорию транзакций.
 * Содержит информацию о категории, такую как идентификатор, название и пользователь, которому она принадлежит.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categories", schema = "app")
public class Category {
    /**
     * Уникальный идентификатор категории.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_seq_generator")
    @SequenceGenerator(name = "category_id_seq_generator", schema = "app", sequenceName = "categories_id_seq", allocationSize = 1)
    private int id;
    /**
     * Название категории.
     */
    private String name;
    /**
     * Пользователь, которому принадлежит категория.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
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
