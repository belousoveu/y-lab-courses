package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.*;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.repository.UserRepository;
import belousov.eu.utils.Password;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class UserService implements AuthService, ProfileService, AdminAccess {
    private final UserRepository userRepository;

    @Override
    public void register(String name, String email, String password) {
        User user = userRepository.save(new User(name, email.toLowerCase(), Password.encode(password)));
        PersonalMoneyTracker.setCurrentUser(user);
    }

    @Override
    public void login(String email, String password) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(email.toLowerCase()));
        if (!user.isActive()) {
            throw new UserWasBlockedException(user.getName());
        }
        checkPassword(user, password);
        PersonalMoneyTracker.setCurrentUser(user);
    }

    @Override
    public void changeName(User user, String name) {
        user.setName(name);
        userRepository.save(user);
    }

    public void changePassword(User user, String oldPassword, String newPassword) {
        checkPassword(user, oldPassword);
        user.setPassword(Password.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void changeEmail(User user, String email) {
        userRepository.removeOldEmail(user.getEmail());
        user.setEmail(email.toLowerCase());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user, String password) {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        if (currentUser.isAdmin()) {
            checkIfLastAdmin(user);
            userRepository.delete(user); //TODO Cascade delete all transactions
        } else if (currentUser.equals(user)) {
            checkPassword(currentUser, password);
            userRepository.delete(user); //TODO Cascade delete all transactions
            PersonalMoneyTracker.setCurrentUser(null);
        } else {
            throw new ForbiddenException();
        }
    }

    public void deleteUserById(int id) {
        User user = findById(id);
        deleteUser(user, "");
    }

    public void blockUser(int id) {
        User user = findById(id);
        checkIfLastAdmin(user);
        user.setActive(false);
        userRepository.save(user);
    }

    public void unblockUser(int id) {
        User user = findById(id);
        user.setActive(true);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void setRole(int userId, Role role) {
        User user = findById(userId);
        if (role == Role.ADMIN) {
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        } else {
            checkIfLastAdmin(user);
            user.setRole(Role.USER);
            userRepository.save(user);
        }
        if (PersonalMoneyTracker.getCurrentUser().getId() == userId) {
            PersonalMoneyTracker.setCurrentUser(user);
        }
    }

    private User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    private void checkPassword(User user, String password) {
        if (!Password.verify(password, user.getPassword())) {
            throw new InvalidPasswordException();
        }
    }

    private void checkIfLastAdmin(User user) {
        List<Integer> adminIds = userRepository.getAllAdminIds();
        if (adminIds.contains(user.getId()) && adminIds.size() == 1) {
            throw new LastAdminDeleteException();
        }
    }
}
