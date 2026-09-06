package com.corporacion.tecnica.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.corporacion.tecnica.dto.usuario.UsuarioResponse;
import com.corporacion.tecnica.dto.usuario.UsuarioUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.mapper.UsuarioMapper;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.SedeRepository;
import com.corporacion.tecnica.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private InstitucionRepository institucionRepository;
    @Mock
    private SedeRepository sedeRepository;

    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        InstitutionScopeResolver institutionScopeResolver = new InstitutionScopeResolver(institucionRepository);
        SedeScopeResolver sedeScopeResolver = new SedeScopeResolver(sedeRepository, institutionScopeResolver);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, usuarioMapper, rolRepository, institutionScopeResolver, sedeScopeResolver);
    }

    @Test
    void actualizarPermiteEditarUsuarioYSacarloDeInstitucionCuandoEsSuperAdmin() {
        Usuario existing = buildUsuario(1L, "Ana Admin", "ana@inst.com", RolNombre.ADMIN_INSTITUCION, buildInstitucion(10L));
        UsuarioUpdateRequest request = buildRequest("Ana Editada", "ana.editada@inst.com", RolNombre.SUPER_ADMIN, null, null, EstadoRegistro.INACTIVO);
        Rol superAdminRole = buildRol(RolNombre.SUPER_ADMIN);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.findByEmail("ana.editada@inst.com")).thenReturn(Optional.empty());
        when(rolRepository.findByNombre(RolNombre.SUPER_ADMIN)).thenReturn(Optional.of(superAdminRole));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioMapper.toResponse(any(Usuario.class))).thenReturn(new UsuarioResponse());

        UsuarioResponse response = usuarioService.actualizar(1L, request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario saved = captor.getValue();

        assertEquals("Ana Editada", saved.getNombre());
        assertEquals("ana.editada@inst.com", saved.getEmail());
        assertEquals(RolNombre.SUPER_ADMIN, saved.getRol().getNombre());
        assertNull(saved.getInstitucion());
        assertNull(saved.getSede());
        assertEquals(EstadoRegistro.INACTIVO, saved.getEstado());
        verify(institucionRepository, never()).findById(any());
        verify(sedeRepository, never()).findById(any());
        assertNotNull(response);
    }

    @Test
    void actualizarAsignaInstitucionCuandoRolEsAdminInstitucion() {
        Usuario existing = buildUsuario(2L, "Carlos Docente", "carlos@inst.com", RolNombre.DOCENTE, null);
        UsuarioUpdateRequest request = buildRequest("Carlos Actualizado", "carlos.actualizado@inst.com", RolNombre.ADMIN_INSTITUCION, 20L, null, null);
        Rol adminRole = buildRol(RolNombre.ADMIN_INSTITUCION);
        Institucion institucion = buildInstitucion(20L);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.findByEmail("carlos.actualizado@inst.com")).thenReturn(Optional.empty());
        when(rolRepository.findByNombre(RolNombre.ADMIN_INSTITUCION)).thenReturn(Optional.of(adminRole));
        when(institucionRepository.findById(20L)).thenReturn(Optional.of(institucion));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioMapper.toResponse(any(Usuario.class))).thenReturn(new UsuarioResponse());

        usuarioService.actualizar(2L, request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario saved = captor.getValue();

        assertEquals(RolNombre.ADMIN_INSTITUCION, saved.getRol().getNombre());
        assertEquals(20L, saved.getInstitucion().getId());
        assertNull(saved.getSede());
    }

    @Test
    void actualizarAsignaSedeCuandoRolEsAdminSede() {
        Usuario existing = buildUsuario(5L, "Sede User", "sede@inst.com", RolNombre.ADMIN_SEDE, buildInstitucion(30L));
        UsuarioUpdateRequest request = buildRequest("Sede User Editado", "sede.editado@inst.com", RolNombre.ADMIN_SEDE, 30L, 200L, null);
        Rol adminSedeRole = buildRol(RolNombre.ADMIN_SEDE);
        Institucion institucion = buildInstitucion(30L);
        Sede sede = buildSede(200L, institucion);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.findByEmail("sede.editado@inst.com")).thenReturn(Optional.empty());
        when(rolRepository.findByNombre(RolNombre.ADMIN_SEDE)).thenReturn(Optional.of(adminSedeRole));
        when(sedeRepository.findById(200L)).thenReturn(Optional.of(sede));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioMapper.toResponse(any(Usuario.class))).thenReturn(new UsuarioResponse());

        usuarioService.actualizar(5L, request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario saved = captor.getValue();

        assertEquals(RolNombre.ADMIN_SEDE, saved.getRol().getNombre());
        assertEquals(30L, saved.getInstitucion().getId());
        assertEquals(200L, saved.getSede().getId());
    }

    @Test
    void actualizarRechazaEmailDuplicadoDeOtroUsuario() {
        Usuario existing = buildUsuario(3L, "Owner", "owner@inst.com", RolNombre.SUPER_ADMIN, null);
        Usuario duplicated = buildUsuario(4L, "Otro", "dup@inst.com", RolNombre.DOCENTE, null);
        UsuarioUpdateRequest request = buildRequest("Owner Editado", "dup@inst.com", RolNombre.SUPER_ADMIN, null, null, null);

        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.findByEmail("dup@inst.com")).thenReturn(Optional.of(duplicated));

        assertThrows(BusinessException.class, () -> usuarioService.actualizar(3L, request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    private UsuarioUpdateRequest buildRequest(String nombre, String email, RolNombre rol, Long institucionId, Long sedeId, EstadoRegistro estado) {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setNombre(nombre);
        request.setEmail(email);
        request.setRol(rol);
        request.setInstitucionId(institucionId);
        request.setSedeId(sedeId);
        request.setEstado(estado);
        return request;
    }

    private Usuario buildUsuario(Long id, String nombre, String email, RolNombre rolNombre, Institucion institucion) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword("encoded");
        usuario.setRol(buildRol(rolNombre));
        usuario.setInstitucion(institucion);
        return usuario;
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
        institucion.setCodigo("INST-" + id);
        institucion.setNombre("Institucion " + id);
        return institucion;
    }

    private Sede buildSede(Long id, Institucion institucion) {
        Sede sede = new Sede();
        sede.setId(id);
        sede.setNombre("Sede " + id);
        sede.setInstitucion(institucion);
        return sede;
    }
}




