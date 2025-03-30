package belousov.eu.controller;

import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;
import belousov.eu.model.entity.User;
import belousov.eu.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);

    }

    @PostMapping("/login")
    public User login(@Valid @RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @GetMapping("/")
    public String home() {
        return "Home Page";
    }
}
