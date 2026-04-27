package org.cook.extracter.service.interfaces;

import org.cook.extracter.security.auth.AuthResponse;
import org.cook.extracter.security.auth.LoginRequest;
import org.cook.extracter.security.auth.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);

}
