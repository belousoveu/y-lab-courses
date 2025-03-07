package belousov.eu.service;

import belousov.eu.model.User;
import belousov.eu.repository.UserRepository;
import belousov.eu.utils.Password;

public class UserService {
    private final UserRepository userRepository = new UserRepository();


    public UserService() {
    }

    public void register(String name, String email, String password) {
        userRepository.save(new User(name, email, Password.encode(password)));
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

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Wrong email or password")); //TODO Custom Exception
        if (Password.verify(password, user.getPassword())) {
            return user;
        }
        throw new RuntimeException("Wrong email or password"); //TODO Custom Exception
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")); //TODO Custom Exception
    }


}
