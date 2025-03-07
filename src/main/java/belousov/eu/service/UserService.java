package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.ForbiddenException;
import belousov.eu.exception.InvalidPasswordException;
import belousov.eu.exception.UserNotFoundException;
import belousov.eu.exception.UserWasBlockedException;
import belousov.eu.model.User;
import belousov.eu.repository.UserRepository;
import belousov.eu.utils.Password;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService implements AuthService, ProfileService{
    private final UserRepository userRepository;

    @Override
    public void register(String name, String email, String password) {
        User user = userRepository.save(new User(name, email, Password.encode(password)));
        PersonalMoneyTracker.setCurrentUser(user);
    }

    @Override
    public void login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        if (!user.isActive()) {
            throw new UserWasBlockedException(user.getName());
        }
        if (!Password.verify(password, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        PersonalMoneyTracker.setCurrentUser(user);
    }

    @Override
    public void changeName(User user, String name) {
        user.setName(name);
        userRepository.save(user);
    }

    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!Password.verify(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        user.setPassword(Password.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void changeEmail(User user, String email) {
        userRepository.removeOldEmail(user.getEmail());
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user, String password) {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        if (!currentUser.equals(user)) {
            if (!Password.verify(password, user.getPassword())) {
                throw new InvalidPasswordException();
            }
            userRepository.delete(user); //TODO Cascade delete all transactions
            PersonalMoneyTracker.setCurrentUser(null);
        } else if (currentUser.isAdmin()) {
                userRepository.delete(user);

        } else {
            throw new ForbiddenException();
        }
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }


}
