package belousov.eu.model.entity;

import lombok.*;

/**
 * Класс, представляющий финансовую цель пользователя.
 * Содержит информацию о цели, такую как идентификатор, пользователь, название, описание и целевое значение (points).
 * Также включает метод toString(), который возвращает строковое представление цели.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class Goal {
    /**
     * Уникальный идентификатор цели.
     */
    private int id;
    /**
     * Пользователь, создавший цель.
     */
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
