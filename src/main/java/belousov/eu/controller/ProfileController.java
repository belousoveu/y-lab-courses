package belousov.eu.controller;

import belousov.eu.model.dto.UserProfileDto;
import belousov.eu.model.dto.UserProfileUpdateDto;
import belousov.eu.model.entity.User;
import belousov.eu.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private static final String CURRENT_USER = "currentUser";

    private final ProfileService profileService;

    @PatchMapping("/{id}")
    public void updateProfile(@PathVariable int id, @RequestBody UserProfileUpdateDto updateDto, HttpSession session) {
        User currentUser = (User) session.getAttribute(CURRENT_USER);
        profileService.updateUser(id, updateDto, currentUser);
    }

    @DeleteMapping("/{id}")
    public void deleteProfile(@PathVariable int id, @RequestBody String password, HttpSession session) {
        User currentUser = (User) session.getAttribute(CURRENT_USER);
        profileService.deleteUser(id, password, currentUser);
    }

    @GetMapping("/{id}")
    public UserProfileDto viewProfile(@PathVariable int id) {
        return profileService.getUserById(id);
    }
}
