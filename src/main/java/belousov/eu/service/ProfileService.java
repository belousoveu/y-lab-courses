package belousov.eu.service;

import belousov.eu.model.User;
import belousov.eu.model.dto.UserProfileDto;
import belousov.eu.model.dto.UserProfileUpdateDto;

public interface ProfileService {

    void deleteUser(int id, String password, User currentUser);

    UserProfileDto getUserById(int id);

    void updateUser(int id, UserProfileUpdateDto updateDto);
}
