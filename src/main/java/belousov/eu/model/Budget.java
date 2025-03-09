package belousov.eu.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@AllArgsConstructor
@Getter
@Setter
public class Budget {
    private int id;
    private YearMonth period;
    private Category category;
    private User user;
    private double amount;
}
