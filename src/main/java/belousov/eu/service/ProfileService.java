package belousov.eu.service;

import belousov.eu.annotation.Loggable;
import belousov.eu.model.User;
import belousov.eu.model.dto.UserProfileDto;
import belousov.eu.model.dto.UserProfileUpdateDto;

public interface ProfileService {

    @Loggable
    void deleteUser(int id, String password, User currentUser);

    UserProfileDto getUserById(int id);

    @Loggable
    void updateUser(int id, UserProfileUpdateDto updateDto, User currentUser);
}
