package belousov.eu.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
public class User {

    private long id;
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

}
