package belousov.eu.observer;

import belousov.eu.model.Transaction;

public interface BalanceChangeObserver {
    void balanceChanged(Transaction lastTransaction);
}
