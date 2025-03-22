package belousov.eu.model.dto;

import java.time.LocalDate;


public record TransactionDto(
        int id,
        LocalDate date,
        String operationType,
        String category,
        double amount,
        String description,
        int userId
) {
}
