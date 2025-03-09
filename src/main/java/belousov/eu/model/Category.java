package belousov.eu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Category {

    private int id;
    private String name;
    private User user;

    @Override
    public String toString() {
        return name;
    }
}
