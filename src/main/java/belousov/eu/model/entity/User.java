package belousov.eu.model.entity;

import lombok.*;

import java.io.Serializable;

/**
 * Класс, представляющий пользователя системы.
 * Содержит информацию о пользователе, такую как идентификатор, имя, email, пароль, роль и статус активности.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class User implements Serializable {

    /**
     * Уникальный идентификатор пользователя.
     */
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
    private Role role = Role.USER;
    /**
     * Статус активности пользователя (активен или заблокирован).
     */
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
