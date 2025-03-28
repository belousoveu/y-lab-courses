package belousov.eu.observer;

import belousov.eu.model.entity.Transaction;

public interface BalanceChangeObserver {
    void balanceChanged(Transaction lastTransaction);
}
