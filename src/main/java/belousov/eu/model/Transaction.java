package belousov.eu.model;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class Transaction {

    private int id;
    private LocalDate date;
    private OperationType operationType;
    private Category category;
    private double amount;
    private String description;
    private User user;

    @Override
    public String toString() {
        return "id=%d, date=%s, operationType=%s, category=%s, amount=%,.2f, description=%s "
                .formatted(id, date, operationType, category.getName(), amount, description);
    }

    public String toStringWithUser() {
        return "id=%d, date=%s, operationType=%s, category=%s, amount=%,.2f, description=%s, user=%s"
                .formatted(id, date, operationType, category.getName(), amount, description, user.getName());
    }
}
