package belousov.eu.repository;

import belousov.eu.model.entity.User;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(@NotNull User user);

    void delete(User user);

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    List<Integer> getAllAdminIds();
}
