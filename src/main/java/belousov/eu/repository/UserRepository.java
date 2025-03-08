package belousov.eu.repository;

import belousov.eu.exception.EmailAlreadyExistsException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;
import belousov.eu.utils.Password;

import java.util.*;

public class UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private final Set<Integer> admins = new HashSet<>();
    private final Set<String> emails = new HashSet<>();
    private final IdGenerator<Integer> idCounter = IdGenerator.create(Integer.class);

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
        users.remove(user.getId());
        admins.remove(user.getId());
        emails.remove(user.getEmail());
    }

    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }

    public void removeOldEmail(String email) {
        emails.remove(email);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public List<Integer> getAllAdminIds() {
        return new ArrayList<>(admins);
    }

}
