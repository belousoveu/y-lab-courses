package belousov.eu.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Класс, представляющий пользователя системы.
 * Содержит информацию о пользователе, такую как идентификатор, имя, email, пароль, роль и статус активности.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "users", schema = "app", indexes = {@Index(name = "idx_user_email", columnList = "email", unique = true)})
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq_generator")
    @SequenceGenerator(name = "user_id_seq_generator", schema = "app", sequenceName = "user_id_seq", allocationSize = 1)
    private int id;
    /**
     * Имя пользователя.
     */
    private String name;
    /**
     * Электронная почта пользователя.
     */
    private String email;
    /**
     * Пароль пользователя, хранится в зашифрованном виде.
     */
    private String password;
    /**
     * Роль пользователя (USER или ADMIN).
     */
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    /**
     * Статус активности пользователя (активен или заблокирован).
     */
    @Column(name = "is_active")
    private boolean active = true;


    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @return true, если роль пользователя ADMIN, иначе false
     */
    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    /**
     * Возвращает строковое представление пользователя.
     *
     * @return строка, содержащая информацию о пользователе
     */
    @Override
    public String toString() {
        return "id=%d, name='%s', email='%s', role=%s, active=%b".formatted(id, name, email, role, active);
    }

}
