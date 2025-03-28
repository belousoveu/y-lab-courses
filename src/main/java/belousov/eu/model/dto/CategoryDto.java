package belousov.eu.model.dto;

import belousov.eu.model.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"name", "user"})
public class CategoryDto implements Validatable {

    private int id;
    @NotNull(message = "Укажите название категории")
    private String name;
    private User user;
}
