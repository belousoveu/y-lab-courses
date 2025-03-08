package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.ForbiddenException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AdminServiceImp implements AdminService {
    private final AdminAccess adminAccess;

    @Override
    public List<User> getAllUsers() {
        checkAccess();
        return adminAccess.getAllUsers();
    }

    @Override
    public void blockUser(int userId) {
        checkAccess();
        adminAccess.blockUser(userId);
        if (PersonalMoneyTracker.getCurrentUser().getId() == userId) {
            PersonalMoneyTracker.setCurrentUser(null);
        }
    }

    @Override
    public void unblockUser(int userId) {
        checkAccess();
        adminAccess.unblockUser(userId);
    }

    @Override
    public void deleteUserById(int userId) {
        checkAccess();
        adminAccess.deleteUserById(userId);
        if (PersonalMoneyTracker.getCurrentUser().getId() == userId) {
            PersonalMoneyTracker.setCurrentUser(null);
        }
    }

    @Override
    public void setRole(int userId, Role role) {
        checkAccess();
        adminAccess.setRole(userId, role);
    }

    private void checkAccess() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new ForbiddenException();
        }
    }
}
