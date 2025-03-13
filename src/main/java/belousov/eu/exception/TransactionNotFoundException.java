package belousov.eu.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(int id) {
        super(String.format("Не найдена транзакция с идентификатором %d", id));
    }
}
