package com.corporacion.tecnica.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private final String token;
    private final String tipo;
    private final String email;
    private final String rol;
    private final Long institucionId;
    private final Long sedeId;
}

