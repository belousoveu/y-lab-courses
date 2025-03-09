package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.ForbiddenException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AdminServiceImp implements AdminService {
    private final AdminAccessUserService adminAccessUserService;
    private final AdminAccessTransactionService adminAccessTransactionService;

    @Override
    public List<User> getAllUsers() {
        checkAccess();
        return adminAccessUserService.getAllUsers();
    }

    @Override
    public void blockUser(int userId) {
        checkAccess();
        adminAccessUserService.blockUser(userId);
        if (PersonalMoneyTracker.getCurrentUser().getId() == userId) {
            PersonalMoneyTracker.setCurrentUser(null);
        }
    }

    @Override
    public void unblockUser(int userId) {
        checkAccess();
        adminAccessUserService.unblockUser(userId);
    }

    @Override
    public List<String> getAllTransactions() {
        checkAccess();
        return adminAccessTransactionService.getAllTransactions();
    }

    @Override
    public void deleteUserById(int userId) {
        checkAccess();
        adminAccessUserService.deleteUserById(userId);
        if (PersonalMoneyTracker.getCurrentUser().getId() == userId) {
            PersonalMoneyTracker.setCurrentUser(null);
        }
    }

    @Override
    public void setRole(int userId, Role role) {
        checkAccess();
        adminAccessUserService.setRole(userId, role);
    }

    private void checkAccess() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new ForbiddenException();
        }
    }
}
