package belousov.eu.config;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.controller.command.*;
import belousov.eu.model.Role;
import belousov.eu.view.Menu;

public class MenuInitializer {

    private static final String BACK_TO_MAIN_MENU_MESSAGE = "Вернуться в главное меню";
    private static final String CATEGORIES_MANAGEMENT_MESSAGE = "Управление категориями";

    private MenuInitializer() {

    }

    public static Menu initializeLoginMenu(DependencyContainer container) {
        Menu registrationMenu = new Menu("Войдите в программу или зарегистрируйтесь");
        registrationMenu.add(1, "Войти", new AuthCommand(container));
        registrationMenu.add(2, "Регистрация", new RegisterCommand(container));
        registrationMenu.add(0, "Завершение программы", () -> PersonalMoneyTracker.setRunning(false));
        return registrationMenu;
    }

    public static Menu initializeMainMenu(DependencyContainer container) {

        Menu mainMenu = new Menu("Главное меню", Role.USER);

        Menu profileMenu = new Menu("Личный кабинет", Role.USER);
        profileMenu.add(1, "Просмотреть профиль", new ViewProfileCommand(container));
        profileMenu.add(2, "Изменить имя пользователя", new ChangeUsernameCommand(container));
        profileMenu.add(3, "Изменить email", new ChangeEmailCommand(container));
        profileMenu.add(4, "Изменить пароль", new ChangePasswordCommand(container));
        profileMenu.add(5, "Удалить аккаунт", new DeleteProfileCommand(container));
        profileMenu.add(0, BACK_TO_MAIN_MENU_MESSAGE, mainMenu::display);

        Menu categoryMenu = new Menu(CATEGORIES_MANAGEMENT_MESSAGE, Role.USER);
        categoryMenu.add(1, "Добавить категорию", new AddCategoryCommand(container));
        categoryMenu.add(2, "Изменить категорию", new ChangeCategoryCommand(container));
        categoryMenu.add(3, "Удалить категорию", new DeleteCategoryCommand(container));
        categoryMenu.add(4, "Посмотреть список категорий", new ListCategoriesCommand(container));
        categoryMenu.add(0, BACK_TO_MAIN_MENU_MESSAGE, () -> categoryMenu.getParent().display());

        Menu financeMenagementMenu = new Menu("Управление личными финансами", Role.USER);
        financeMenagementMenu.add(1, "Добавить операцию", new AddTransactionCommand(container));
        financeMenagementMenu.add(2, "Изменить операцию", new UpdateTransactionCommand(container));
        financeMenagementMenu.add(3, "Удалить операцию", new DeleteTransactionComand(container));
        financeMenagementMenu.add(4, "Посмотреть историю операций", new ViewTransactionCommand(container));
        financeMenagementMenu.add(5, CATEGORIES_MANAGEMENT_MESSAGE, () -> {
            categoryMenu.setParent(financeMenagementMenu);
            categoryMenu.display();
        });
        financeMenagementMenu.add(0, BACK_TO_MAIN_MENU_MESSAGE, mainMenu::display);

        Menu budgetMenu = new Menu("Бюджет", Role.USER);
        budgetMenu.add(1, "Установить бюджет месяц", new SetBudgetCommand(container));
        budgetMenu.add(2, "Отчет по бюджету", new ViewBudgetCommand(container));
        budgetMenu.add(3, CATEGORIES_MANAGEMENT_MESSAGE, () -> {
            categoryMenu.setParent(budgetMenu);
            categoryMenu.display();
        });
        budgetMenu.add(0, BACK_TO_MAIN_MENU_MESSAGE, mainMenu::display);

        Menu goalsMenu = new Menu("Управление целями", Role.USER);
        goalsMenu.add(1, "Добавить цель", new AddGoalCommand(container));
        goalsMenu.add(2, "Изменить цель", new EditGoalCommand(container));
        goalsMenu.add(3, "Удалить цель", new DeleteGoalCommand(container));
        goalsMenu.add(4, "Посмотреть список целей", new ViewGoalCommand(container));
        goalsMenu.add(0, BACK_TO_MAIN_MENU_MESSAGE, mainMenu::display);

        Menu reportMenu = new Menu("Статистика и аналитика", Role.USER);
        reportMenu.add(1, "Текущий баланс", new ViewBalancCommand(container));
        reportMenu.add(2, "Отчет о доходах и расходах за период", new ViewIncomeStatementCommand(container));
        reportMenu.add(3, "Отчет о расходах по категориям", new ViewCategoryReportCommand(container));
        reportMenu.add(4, "Отчет о финансовом состоянии", new ViewFinancialReportCommand(container));
        reportMenu.add(0, BACK_TO_MAIN_MENU_MESSAGE, mainMenu::display);

        Menu adminMenu = new Menu("Администрирование", Role.ADMIN);
        adminMenu.add(1, "Список пользователей", new ListUsersCommand(container), Role.ADMIN);
        adminMenu.add(2, "Список операций", new ListOperationsCommand(container), Role.ADMIN);
        adminMenu.add(3, "Изменить роль пользователя", new ChangeRoleCommand(container), Role.ADMIN);
        adminMenu.add(4, "Заблокировать пользователя", new BlockUserCommand(container), Role.ADMIN);
        adminMenu.add(5, "Разблокировать пользователя", new UnblockUserCommand(container), Role.ADMIN);
        adminMenu.add(6, "Удалить пользователя", new DeleteUserCommand(container), Role.ADMIN);
        adminMenu.add(0, BACK_TO_MAIN_MENU_MESSAGE, mainMenu::display);


        mainMenu.add(1, "Личный кабинет", profileMenu::display);
        mainMenu.add(2, "Управление личными финансами", financeMenagementMenu::display);
        mainMenu.add(3, "Управление бюджетом", budgetMenu::display);
        mainMenu.add(4, "Управление целями", goalsMenu::display);
        mainMenu.add(5, "Статистика и аналитика", reportMenu::display);
        mainMenu.add(9, "Администрирование", adminMenu::display, Role.ADMIN);
        mainMenu.add(0, "Выйти из аккаунта", () -> PersonalMoneyTracker.setCurrentUser(null));

        return mainMenu;
    }
}
