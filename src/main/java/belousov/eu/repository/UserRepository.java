package belousov.eu.repository;

import belousov.eu.exception.EmailAlreadyExistsException;
import belousov.eu.exception.LastAdminDeleteException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;
import belousov.eu.utils.Password;

import java.util.*;

public class UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<Long> admins = new HashSet<>();
    private final Set<String> emails = new HashSet<>();
    private final IdGenerator<Long> idCounter = IdGenerator.create(Long.class);

    public UserRepository() {
        init();
    }

    private void init() {
        String encodedPassword = Password.encode("Admin123");
        save(new User(idCounter.nextId(), "admin", "admin@admin.com", encodedPassword, Role.ADMIN, true));
    }

    public User save(User user) {
        if (user.getId() == 0 && emails.contains(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        if (user.getId() == 0) {
            user.setId(idCounter.nextId());
        }

        users.put(user.getId(), user);
        if (user.getRole() == Role.ADMIN) {
            admins.add(user.getId());
        }
        emails.add(user.getEmail());
        return user;
    }

    public void delete(User user) {
        if (admins.contains(user.getId()) && admins.size() == 1) {
            throw new LastAdminDeleteException();
        }
        users.remove(user.getId());
        admins.remove(user.getId());
        emails.remove(user.getEmail());
    }

    public Optional<User> findById(long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }

    public void removeOldEmail(String email) {
        emails.remove(email);
    }
}
