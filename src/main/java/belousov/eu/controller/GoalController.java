package belousov.eu.controller;

import belousov.eu.model.dto.GoalDto;
import belousov.eu.model.entity.User;
import belousov.eu.service.GoalService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/goals")
public class GoalController {

    private static final String CURRENT_USER = "currentUser";

    private final GoalService goalService;

    @PutMapping
    public void addGoal(@RequestBody GoalDto goalDto, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        goalService.addGoal(user, goalDto);
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        goalService.deleteGoal(id, user);
    }

    @PutMapping("/{id}")
    public void editGoal(@PathVariable int id, @RequestBody GoalDto goalDto, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        goalService.editGoal(id, user, goalDto);
    }


    @GetMapping
    public List<GoalDto> getAllGoals(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return goalService.getAllByUserId(user.getId());

    }

}
