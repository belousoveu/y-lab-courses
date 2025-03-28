package belousov.eu.observer;

import belousov.eu.model.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

public class BalanceChangeSubject {
    private final List<BalanceChangeObserver> observers = new ArrayList<>();

    public void addObserver(BalanceChangeObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BalanceChangeObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Transaction lastTransaction) {
        for (BalanceChangeObserver observer : observers) {
            observer.balanceChanged(lastTransaction);
        }
    }

}
