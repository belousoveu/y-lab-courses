package belousov.eu.service.imp;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.ForbiddenException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;
import belousov.eu.service.AdminAccessTransactionService;
import belousov.eu.service.AdminAccessUserService;
import belousov.eu.service.AdminService;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Реализация сервиса для административных действий.
 * Обеспечивает управление пользователями и транзакциями с проверкой прав доступа.
 */
@AllArgsConstructor
public class AdminServiceImp implements AdminService {
    /**
     * Сервис для управления пользователями.
     */
    private final AdminAccessUserService adminAccessUserService;
    /**
     * Сервис для управления транзакциями.
     */
    private final AdminAccessTransactionService adminAccessTransactionService;

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     * @throws ForbiddenException если текущий пользователь не является администратором
     */
    @Override
    public List<UserDto> getAllUsers() {
        checkAccess();
        return adminAccessUserService.getAllUsers();
    }

    /**
     * Блокирует пользователя по ID.
     *
     * @param userId ID пользователя
     * @throws ForbiddenException если текущий пользователь не является администратором
     */
    @Override
    public void blockUser(int userId) {
        checkAccess();
        adminAccessUserService.blockUser(userId);
        if (PersonalMoneyTracker.getCurrentUser().getId() == userId) {
            PersonalMoneyTracker.setCurrentUser(null);
        }
    }

    /**
     * Разблокирует пользователя по ID.
     *
     * @param userId ID пользователя
     * @throws ForbiddenException если текущий пользователь не является администратором
     */
    @Override
    public void unblockUser(int userId) {
        checkAccess();
        adminAccessUserService.unblockUser(userId);
    }

    /**
     * Возвращает список всех транзакций.
     *
     * @return список всех транзакций
     * @throws ForbiddenException если текущий пользователь не является администратором
     */
    @Override
    public List<TransactionDto> getAllTransactions() {
        checkAccess();
        return adminAccessTransactionService.getAllTransactions();
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param userId ID пользователя
     * @throws ForbiddenException если текущий пользователь не является администратором
     */
    @Override
    public void deleteUserById(int userId) {
        checkAccess();
        adminAccessUserService.deleteUserById(userId);
        if (PersonalMoneyTracker.getCurrentUser().getId() == userId) {
            PersonalMoneyTracker.setCurrentUser(null);
        }
    }

    /**
     * Устанавливает роль пользователю по ID.
     *
     * @param userId ID пользователя
     * @param role   новая роль
     * @throws ForbiddenException если текущий пользователь не является администратором
     */
    @Override
    public void setRole(int userId, Role role) {
        checkAccess();
        adminAccessUserService.setRole(userId, role);
    }

    /**
     * Проверяет, имеет ли текущий пользователь права администратора.
     *
     * @throws ForbiddenException если текущий пользователь не является администратором
     */
    private void checkAccess() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new ForbiddenException();
        }
    }
}
