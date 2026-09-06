package com.corporacion.tecnica.service.impl.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.corporacion.tecnica.dto.auth.AuthResponse;
import com.corporacion.tecnica.dto.auth.RegisterRequest;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.SedeRepository;
import com.corporacion.tecnica.repository.UsuarioRepository;
import com.corporacion.tecnica.security.JwtService;
import com.corporacion.tecnica.service.impl.InstitutionScopeResolver;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;
    @Mock
    private InstitucionRepository institucionRepository;
    @Mock
    private SedeRepository sedeRepository;

    private AuthServiceImpl authService;
    private JwtService jwtService;
    private InstitutionScopeResolver institutionScopeResolver;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "test-secret-key-test-secret-key-test");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L);
        institutionScopeResolver = new InstitutionScopeResolver(institucionRepository);
        authService = new AuthServiceImpl(authenticationManager, usuarioRepository, rolRepository, passwordEncoder, jwtService, institutionScopeResolver, sedeRepository);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerPermiteBootstrapDeSuperAdminSinInstitucion() {
        RegisterRequest request = buildRequest("Dueño", "owner@teccore.com", "secret", RolNombre.SUPER_ADMIN, null, null);
        Rol rol = buildRol(RolNombre.SUPER_ADMIN);

        when(usuarioRepository.count()).thenReturn(0L);
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(rolRepository.findByNombre(RolNombre.SUPER_ADMIN)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-secret");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });
        AuthResponse response = authService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario savedUser = captor.getValue();

        assertNull(savedUser.getInstitucion());
        assertEquals(RolNombre.SUPER_ADMIN, savedUser.getRol().getNombre());
        assertNotNull(response.getToken());
        assertNull(response.getInstitucionId());
        assertNull(response.getSedeId());
        verify(institucionRepository, never()).findById(any());
        verify(sedeRepository, never()).findById(any());
    }

    @Test
    void registerRechazaRegistroSinSuperAdminAutenticadoCuandoYaExisteBootstrap() {
        RegisterRequest request = buildRequest("Admin", "admin@inst.com", "secret", RolNombre.ADMIN_INSTITUCION, 10L, null);
        when(usuarioRepository.count()).thenReturn(1L);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> authService.register(request));

        assertTrue(exception.getMessage().contains("No autorizado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registerPermiteASuperAdminCrearAdministradorConInstitucion() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "owner@teccore.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))));

        RegisterRequest request = buildRequest("Admin Inst", "admin@inst.com", "secret", RolNombre.ADMIN_INSTITUCION, 10L, null);
        Rol rol = buildRol(RolNombre.ADMIN_INSTITUCION);
        Institucion institucion = buildInstitucion(10L);

        when(usuarioRepository.count()).thenReturn(1L);
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(rolRepository.findByNombre(RolNombre.ADMIN_INSTITUCION)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-secret");
        when(institucionRepository.findById(10L)).thenReturn(Optional.of(institucion));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(2L);
            return usuario;
        });
        AuthResponse response = authService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario savedUser = captor.getValue();

        assertNotNull(savedUser.getInstitucion());
        assertEquals(10L, savedUser.getInstitucion().getId());
        assertEquals(RolNombre.ADMIN_INSTITUCION, savedUser.getRol().getNombre());
        assertNull(savedUser.getSede());
        assertEquals(10L, response.getInstitucionId());
        assertNull(response.getSedeId());
    }

    @Test
    void registerImpideQueSuperAdminCreeRolesDistintosAAdministrador() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "owner@teccore.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))));

        RegisterRequest request = buildRequest("Docente", "docente@inst.com", "secret", RolNombre.DOCENTE, 10L, null);
        when(usuarioRepository.count()).thenReturn(1L);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> authService.register(request));

        assertTrue(exception.getMessage().contains("solo puede registrar usuarios ADMIN_INSTITUCION"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registerPermiteAAdminInstitucionCrearAdminSedeDeSuInstitucion() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "admin@institucion.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN_INSTITUCION"))));

        RegisterRequest request = buildRequest("Admin Sede", "admin.sede@inst.com", "secret", RolNombre.ADMIN_SEDE, 10L, 100L);
        Rol rol = buildRol(RolNombre.ADMIN_SEDE);
        Institucion institucion = buildInstitucion(10L);
        Sede sede = buildSede(100L, institucion);

        when(usuarioRepository.count()).thenReturn(2L);
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(rolRepository.findByNombre(RolNombre.ADMIN_SEDE)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-secret");
        when(sedeRepository.findById(100L)).thenReturn(Optional.of(sede));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(3L);
            return usuario;
        });

        AuthResponse response = authService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario savedUser = captor.getValue();

        assertEquals(RolNombre.ADMIN_SEDE, savedUser.getRol().getNombre());
        assertEquals(10L, savedUser.getInstitucion().getId());
        assertEquals(100L, savedUser.getSede().getId());
        assertEquals(10L, response.getInstitucionId());
        assertEquals(100L, response.getSedeId());
    }

    private RegisterRequest buildRequest(String nombre, String email, String password, RolNombre rol, Long institucionId, Long sedeId) {
        RegisterRequest request = new RegisterRequest();
        request.setNombre(nombre);
        request.setEmail(email);
        request.setPassword(password);
        request.setRol(rol);
        request.setInstitucionId(institucionId);
        request.setSedeId(sedeId);
        return request;
    }

    private Rol buildRol(RolNombre nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion("Rol " + nombre.name());
        return rol;
    }

    private Institucion buildInstitucion(Long id) {
        Institucion institucion = new Institucion();
        institucion.setId(id);
        institucion.setCodigo("INST-01");
        institucion.setNombre("Institucion Demo");
        return institucion;
    }

    private Sede buildSede(Long id, Institucion institucion) {
        Sede sede = new Sede();
        sede.setId(id);
        sede.setNombre("Sede Centro");
        sede.setInstitucion(institucion);
        return sede;
    }
}



