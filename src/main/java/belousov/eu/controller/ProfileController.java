package belousov.eu.controller;

import belousov.eu.model.User;
import belousov.eu.model.dto.UserProfileDto;
import belousov.eu.model.dto.UserProfileUpdateDto;
import belousov.eu.service.ProfileService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    public void updateProfile(int id, UserProfileUpdateDto updateDto, User currentUser) {
        profileService.updateUser(id, updateDto, currentUser);
    }

    public void deleteProfile(int id, String password, User currentUser) {
        profileService.deleteUser(id, password, currentUser);
    }

    public UserProfileDto viewProfile(int id) {
        return profileService.getUserById(id);
    }
}
