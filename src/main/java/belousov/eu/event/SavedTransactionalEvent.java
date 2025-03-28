package belousov.eu.event;

import belousov.eu.model.entity.Transaction;
import org.springframework.context.ApplicationEvent;

public class SavedTransactionalEvent extends ApplicationEvent {

    private Transaction transaction;

    public SavedTransactionalEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
