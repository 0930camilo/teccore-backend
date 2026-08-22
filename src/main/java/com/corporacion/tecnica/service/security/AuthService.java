package com.corporacion.tecnica.service.security;

import com.corporacion.tecnica.dto.auth.AuthResponse;
import com.corporacion.tecnica.dto.auth.LoginRequest;
import com.corporacion.tecnica.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}

