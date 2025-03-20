package belousov.eu.service;

import belousov.eu.model.User;
import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;

public interface AuthService {

    User register(RegisterDto registerDto);

    User login(LoginDto loginDto);
}
