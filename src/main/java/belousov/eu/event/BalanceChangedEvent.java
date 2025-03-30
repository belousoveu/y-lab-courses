package belousov.eu.event;

import belousov.eu.model.entity.Transaction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
@EqualsAndHashCode(callSuper = false)
public class BalanceChangedEvent extends ApplicationEvent {

    @EqualsAndHashCode.Include
    private final Transaction transaction;

    public BalanceChangedEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
