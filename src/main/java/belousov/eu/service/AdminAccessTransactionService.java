package belousov.eu.service;

import belousov.eu.model.dto.TransactionDto;

import java.util.List;

public interface AdminAccessTransactionService {

    List<TransactionDto> getAllTransactions();
}
