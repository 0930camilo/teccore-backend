package com.corporacion.tecnica.service.impl.security;

import com.corporacion.tecnica.dto.auth.AuthResponse;
import com.corporacion.tecnica.dto.auth.LoginRequest;
import com.corporacion.tecnica.dto.auth.RegisterRequest;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.UsuarioRepository;
import com.corporacion.tecnica.security.JwtService;
import com.corporacion.tecnica.security.UserPrincipal;
import com.corporacion.tecnica.service.impl.InstitutionScopeResolver;
import com.corporacion.tecnica.service.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales invalidas"));

        String token = jwtService.generateToken(new UserPrincipal(usuario));
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .rol(usuario.getRol().getNombre().name())
                .institucionId(usuario.getInstitucion() != null ? usuario.getInstitucion().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegistrationPolicy(request);

        usuarioRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new BusinessException("Ya existe un usuario con ese email");
        });

        Rol rol = rolRepository.findByNombre(request.getRol())
                .orElseThrow(() -> new BusinessException("Rol no configurado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        if (request.getRol() == RolNombre.SUPER_ADMIN) {
            usuario.setInstitucion(null);
        } else {
            usuario.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        }

        Usuario guardado = usuarioRepository.save(usuario);
        String token = jwtService.generateToken(new UserPrincipal(guardado));

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .email(guardado.getEmail())
                .rol(guardado.getRol().getNombre().name())
                .institucionId(guardado.getInstitucion() != null ? guardado.getInstitucion().getId() : null)
                .build();
    }

    private void validateRegistrationPolicy(RegisterRequest request) {
        if (request.getRol() == null) {
            throw new BusinessException("Debe enviar el rol del usuario");
        }

        boolean initialSetup = usuarioRepository.count() == 0;
        if (initialSetup) {
            if (request.getRol() != RolNombre.SUPER_ADMIN) {
                throw new BusinessException("El primer usuario del sistema debe ser SUPER_ADMIN");
            }
            if (request.getInstitucionId() != null) {
                throw new BusinessException("El SUPER_ADMIN no debe tener institucion asignada");
            }
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getAuthorities().stream().noneMatch(a -> "ROLE_SUPER_ADMIN".equals(a.getAuthority()))) {
            throw new AccessDeniedException("Solo el SUPER_ADMIN puede registrar administradores");
        }

        if (request.getRol() != RolNombre.ADMIN_INSTITUCION) {
            throw new AccessDeniedException("El SUPER_ADMIN solo puede registrar usuarios ADMIN_INSTITUCION");
        }

        if (request.getInstitucionId() == null) {
            throw new BusinessException("Debe enviar institucionId para un ADMIN_INSTITUCION");
        }
    }
}

