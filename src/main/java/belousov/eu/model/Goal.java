package belousov.eu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс, представляющий финансовую цель пользователя.
 * Содержит информацию о цели, такую как идентификатор, пользователь, название, описание и целевое значение (points).
 * Также включает метод toString(), который возвращает строковое представление цели.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "goals", schema = "app")
public class Goal {
    /**
     * Уникальный идентификатор цели.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "goal_id_seq_generator")
    @SequenceGenerator(name = "goal_id_seq_generator", schema = "app", sequenceName = "goal_id_seq", allocationSize = 1)
    private int id;
    /**
     * Пользователь, создавший цель.
     */
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    /**
     * Название цели.
     */
    private String name;
    /**
     * Описание цели.
     */
    private String description;
    /**
     * Целевое значение цели (points).
     */
    private double point;

    /**
     * Возвращает строковое представление цели.
     *
     * @return строка, содержащая информацию о цели
     */
    @Override
    public String toString() {
        return "id=%d, name='%s', description='%s', points=%,.2f".formatted(id, name, description, point);
    }
}
