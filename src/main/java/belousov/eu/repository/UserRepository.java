package belousov.eu.repository;

import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;

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
        User user = new User(idCounter.nextId(), "admin", "admin@admin.com", "admin", Role.ADMIN);
        users.put(user.getId(), user);
        admins.add(user.getId());
        emails.add(user.getEmail());
    }

    public void save(User user) {
        if (user.getId() == 0 && emails.contains(user.getEmail())) {
            throw new RuntimeException("Email is already taken"); //TODO Custom Exception
        }
        if (user.getId() == 0) {
            user.setId(idCounter.nextId());
        }
        users.put(user.getId(), user);
        emails.add(user.getEmail());
    }

    public void delete(User user) {
        if (admins.contains(user.getId()) && admins.size() == 1) {
            throw new RuntimeException("Can't delete last admin"); //TODO Custom Exception
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
