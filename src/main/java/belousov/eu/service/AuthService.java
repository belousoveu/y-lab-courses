package belousov.eu.service;

import belousov.eu.annotation.Loggable;
import belousov.eu.model.User;
import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;


public interface AuthService {

    @Loggable
    User register(RegisterDto registerDto);

    @Loggable
    User login(LoginDto loginDto);
}
