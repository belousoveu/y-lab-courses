package belousov.eu.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})

public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean active=true;

    public User(String name, String email, String password) {
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.name = name;
    }

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    @Override
    public String toString() {
        return "id=%d, name='%s', email='%s', role=%s, active=%b".formatted(id, name, email, role, active);
    }

}
