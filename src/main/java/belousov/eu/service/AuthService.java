package belousov.eu.service;

import belousov.eu.annotation.Loggable;
import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;
import belousov.eu.model.entity.User;


public interface AuthService {

    @Loggable
    User register(RegisterDto registerDto);

    @Loggable
    User login(LoginDto loginDto);
}
