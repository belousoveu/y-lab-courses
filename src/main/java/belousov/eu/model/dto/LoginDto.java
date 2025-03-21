package belousov.eu.model.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginDto(
        @NotNull(message = "Укажите адрес электронной почты")
        @Email(message = "Укажите корректный адрес электронной почты")
        String email,

        @NotNull(message = "Укажите пароль")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Пароль должен содержать хотя бы одну букву и одну цифру и быть не короче 8 символов")
        String password)
        implements Validatable {
}
