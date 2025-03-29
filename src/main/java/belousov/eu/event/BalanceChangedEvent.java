package belousov.eu.event;

import belousov.eu.model.entity.Transaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BalanceChangedEvent extends ApplicationEvent {

    private final Transaction transaction;

    public BalanceChangedEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
