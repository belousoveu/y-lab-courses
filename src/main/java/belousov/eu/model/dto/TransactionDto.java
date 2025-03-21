package belousov.eu.model.dto;

import belousov.eu.model.OperationType;

import java.time.LocalDate;

public record TransactionDto(
        int id,
        LocalDate date,
        OperationType operationType,
        String category,
        double amount,
        String description,
        int userId
) {
}
