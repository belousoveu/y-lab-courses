package belousov.eu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Goal {
    private int id;
    private User user;
    private String name;
    private String description;
    private double point;


    @Override
    public String toString() {
        return "id=%d, name='%s', description='%s', points=%,.2f".formatted(id, name, description, point);
    }
}
