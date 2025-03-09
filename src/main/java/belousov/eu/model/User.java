package belousov.eu.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий пользователя системы.
 * Содержит информацию о пользователе, такую как идентификатор, имя, email, пароль, роль и статус активности.
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class User {

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
    private Role role;
    /**
     * Статус активности пользователя (активен или заблокирован).
     */
    private boolean active=true;

    /**
     * Конструктор для создания нового пользователя с ролью USER.
     *
     * @param name     имя пользователя
     * @param email    электронная почта пользователя
     * @param password пароль пользователя, хранится в зашифрованном виде
     */
    public User(String name, String email, String password) {
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.name = name;
    }

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
