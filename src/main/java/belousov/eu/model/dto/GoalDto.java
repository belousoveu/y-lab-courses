package belousov.eu.model.dto;

import belousov.eu.model.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GoalDto(
        int id,
        User user,
        @NotNull(message = "Укажите название цели")
        String name,
        String description,
        @Positive(message = "Число должно быть положительным")
        double point
)
        implements Validatable {
}
