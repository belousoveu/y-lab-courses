package belousov.eu.service;

import belousov.eu.model.User;

public interface ProfileService {
    void changeName(User currentUser, String newName);

    void changeEmail(User currentUser, String newEmail);

    void changePassword(User currentUser, String oldPassword, String newPassword);

    void deleteUser(User currentUser, String password);
}
