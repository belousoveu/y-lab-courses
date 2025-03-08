package belousov.eu;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.command.*;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.view.Menu;
import lombok.Getter;
import lombok.Setter;

public class PersonalMoneyTracker {

    @Getter
    private static DependencyContainer container;
    @Getter
    @Setter
    private static User currentUser;


    public static void main(String[] args) {

        container = new DependencyContainer();


        Menu registrationMenu = new Menu("Войдите в программу или зарегистрируйтесь");
        registrationMenu.add(1, "Войти", new AuthCommand(container));
        registrationMenu.add(2, "Регистрация", new RegisterCommand(container));
        registrationMenu.add(0, "Завершение программы", () -> System.exit(0));

        Menu mainMenu = new Menu("Главное меню", Role.USER);

        Menu profileMenu = new Menu("Личный кабинет", Role.USER);
        profileMenu.add(1, "Просмотреть профиль", new ViewProfileCommand(container));
        profileMenu.add(2, "Изменить имя пользователя", new ChangeUsernameCommand(container));
        profileMenu.add(3, "Изменить email", new ChangeEmailCommand(container));
        profileMenu.add(4, "Изменить пароль", new ChangePasswordCommand(container));
        profileMenu.add(5, "Удалить аккаунт", new DeleteProfileCommand(container));
        profileMenu.add(0, "Вернуться в главное меню", mainMenu::display);

        Menu categoryMenu = new Menu("Управление категориями", Role.USER);
        categoryMenu.add(1, "Добавить категорию", () -> System.out.println("Добавить категорию"));
        categoryMenu.add(2, "Изменить категорию", () -> System.out.println("Изменить категорию"));
        categoryMenu.add(3, "Удалить категорию", () -> System.out.println("Удалить категорию"));
        categoryMenu.add(4,"Посмотреть список категорий", () -> System.out.println("Посмотреть список категорий"));
        categoryMenu.add(0, "Вернуться в главное меню", () -> categoryMenu.getParent().display());

        Menu financeMenagementMenu = new Menu("Управление личными финансами", Role.USER);
        financeMenagementMenu.add(1, "Добавить операцию", () -> System.out.println("Добавить операцию"));
        financeMenagementMenu.add(2, "Изменить операцию", () -> System.out.println("Изменить трансакцию"));
        financeMenagementMenu.add(3, "Удалить операцию", () -> System.out.println("Удалить трансакцию"));
        financeMenagementMenu.add(4, "Посмотреть историю операций", () -> System.out.println("Посмотреть историю операций"));
        financeMenagementMenu.add(5, "Управление категориями",
                () -> {
            categoryMenu.setParent(financeMenagementMenu);
            categoryMenu.display();}
        );
        financeMenagementMenu.add(0, "Вернуться в главное меню", mainMenu::display);

        Menu budgetMenu = new Menu("Бюджет", Role.USER);
        budgetMenu.add(1, "Установить общий бюджет", () -> System.out.println("Установить бюджет"));
        budgetMenu.add(2, "Установить бюджет для каждой категории", () -> System.out.println("Установить бюджет для каждой категории"));
        budgetMenu.add(3,"Отчет по бюджету", () -> System.out.println("Просмотреть бюджет"));
        budgetMenu.add(4,"Управление категориями", () -> {
            categoryMenu.setParent(budgetMenu);
            categoryMenu.display();}
        );
        budgetMenu.add(0, "Вернуться в главное меню", mainMenu::display);

        Menu goalsMenu = new Menu("Управление целями", Role.USER);
        goalsMenu.add(1, "Добавить цель", () -> System.out.println("Добавить цель"));
        goalsMenu.add(2, "Изменить цель", () -> System.out.println("Изменить цель"));
        goalsMenu.add(3, "Удалить цель", () -> System.out.println("Удалить цель"));
        goalsMenu.add(4, "Посмотреть список целей", () -> System.out.println("Посмотреть список целей"));
        goalsMenu.add(0, "Вернуться в главное меню", mainMenu::display);

        Menu reportMenu = new Menu("Статистика и аналитика", Role.USER);
        reportMenu.add(1, "Текущий баланс", () -> System.out.println("Текущий баланс"));
        reportMenu.add(2, "Отчет о доходах и расходах за период", () -> System.out.println("Отчет о доходах и расходах за период"));
        reportMenu.add(3, "Отчет о расходах по категориям", () -> System.out.println("Отчет о доходах и расходах по категориям"));
        reportMenu.add(4, "Отчет о финансовом состоянии", () -> System.out.println("Отчет о финансовом состоянии"));
        reportMenu.add(0, "Вернуться в главное меню", mainMenu::display);

        Menu adminMenu = new Menu("Администрирование", Role.ADMIN);
        adminMenu.add(1, "Список пользователей", new ListUsersCommand(container), Role.ADMIN);
        adminMenu.add(2, "Список операций", () -> System.out.println("Список операций"), Role.ADMIN);
        adminMenu.add(3, "Изменить роль пользователя", new ChangeRoleCommand(container), Role.ADMIN);
        adminMenu.add(4, "Заблокировать пользователя", new BlockUserCommand(container), Role.ADMIN);
        adminMenu.add(5, "Разблокировать пользователя", new UnblockUserCommand(container), Role.ADMIN);
        adminMenu.add(6, "Удалить пользователя", new DeleteUserCommand(container), Role.ADMIN);
        adminMenu.add(0, "Вернуться в главное меню", mainMenu::display);


        mainMenu.add(1, "Личный кабинет", profileMenu::display);
        mainMenu.add(2, "Управление личными финансами", financeMenagementMenu::display);
        mainMenu.add(3, "Управление бюджетом", budgetMenu::display);
        mainMenu.add(4, "Управление целями", goalsMenu::display);
        mainMenu.add(5, "Статистика и аналитика", reportMenu::display);
        mainMenu.add(9, "Администрирование", adminMenu::display, Role.ADMIN);



        mainMenu.add(0, "Выйти из аккаунта", () -> PersonalMoneyTracker.currentUser = null);

        while (true) {
            registrationMenu.display();
            mainMenu.display();
        }
    }

}