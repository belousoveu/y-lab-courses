package belousov.eu.controller;

import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;
import belousov.eu.model.entity.User;
import belousov.eu.service.AuthService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthController {

    private final AuthService authService;


    public User register(RegisterDto registerDto) {
        return authService.register(registerDto);

    }

    public User login(LoginDto loginDto) {
        return authService.login(loginDto);
    }
}
