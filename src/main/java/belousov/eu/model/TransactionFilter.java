package belousov.eu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransactionFilter {

    User user;
    LocalDate from;
    LocalDate to;
    Category category;
    OperationType type;

}
