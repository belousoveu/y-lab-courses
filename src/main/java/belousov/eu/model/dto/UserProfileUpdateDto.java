package belousov.eu.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserProfileUpdateDto(

        @Size(min = 2, max = 30, message = "Имя пользователя должно быть от 2 до 30 символов")
        @Nullable
        String name,

        @Email(message = "Укажите корректный адрес электронной почты")
        @Nullable
        String email,

        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Пароль должен содержать хотя бы одну букву и одну цифру и быть не короче 8 символов")
        @Nullable
        String oldPassword,
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Пароль должен содержать хотя бы одну букву и одну цифру и быть не короче 8 символов")
        @Nullable
        String password
)
        implements Validatable {
}
