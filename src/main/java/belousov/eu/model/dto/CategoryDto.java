package belousov.eu.model.dto;

import belousov.eu.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto implements Validatable {

    private int id;
    @NotNull(message = "Укажите название категории")
    private String name;
    private User user;
}
