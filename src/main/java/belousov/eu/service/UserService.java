package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.model.User;
import belousov.eu.repository.UserRepository;
import belousov.eu.utils.Password;

public class UserService implements AuthService{
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String name, String email, String password) {
        User user = userRepository.save(new User(name, email, Password.encode(password)));
        PersonalMoneyTracker.setCurrentUser(user);
    }

    public void login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Wrong email or password"));//TODO Custom Exception
        if (!Password.verify(password, user.getPassword())) {
            throw new RuntimeException("Wrong password"); //TODO Custom Exception
        }
        PersonalMoneyTracker.setCurrentUser(user);
    }

    public void changeUsername(User user, String name) {
        user.setName(name);
        userRepository.save(user);
    }

    public void changePassword(User user, String password) {
        user.setPassword(Password.encode(password));
        userRepository.save(user);
    }

    public void changeEmail(User user, String email) {
        userRepository.removeOldEmail(user.getEmail());
        user.setEmail(email);
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")); //TODO Custom Exception
    }


}
