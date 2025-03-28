package belousov.eu.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
public class BudgetDto implements Validatable {

    private int id;
    @NotNull
    private YearMonth period;
    @NotNull
    private Integer categoryId;
    @NotNull
    private Integer userId;
    @Positive
    private Double amount;

}
