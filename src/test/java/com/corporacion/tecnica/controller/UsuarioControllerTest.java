package com.corporacion.tecnica.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.UsuarioRepository;
import com.corporacion.tecnica.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private InstitucionRepository institucionRepository;

    private Institucion institucionNorte;
    private Institucion institucionSur;

    @BeforeEach
    void setUp() {
        Rol superAdminRole = ensureRole(RolNombre.SUPER_ADMIN);
        Rol adminRole = ensureRole(RolNombre.ADMIN_INSTITUCION);
        Rol docenteRole = ensureRole(RolNombre.DOCENTE);

        institucionNorte = createInstitution("INST-NORTE", "Institucion Norte");
        institucionSur = createInstitution("INST-SUR", "Institucion Sur");

        createUser("Owner Global", "owner@test.com", superAdminRole, null);
        createUser("Ana Admin", "ana.admin@test.com", adminRole, institucionNorte);
        createUser("Carlos Docente", "carlos.docente@test.com", docenteRole, institucionNorte);
        createUser("Andrea Docente", "andrea.docente@test.com", docenteRole, institucionSur);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Test
    void listarUsuariosDebeSoportarFiltrosPorRolNombreEInstitucion() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(user("owner@test.com").roles("SUPER_ADMIN"))
                        .param("rol", "DOCENTE")
                        .param("nombre", "Andrea")
                        .param("institucionId", institucionSur.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Listado de usuarios"))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].nombre").value("Andrea Docente"))
                .andExpect(jsonPath("$.data.content[0].rol").value("DOCENTE"))
                .andExpect(jsonPath("$.data.content[0].institucionId").value(institucionSur.getId()))
                .andExpect(jsonPath("$.data.content[0].institucionNombre").value("Institucion Sur"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void listarUsuariosDebeRetornarPaginacionComoInstituciones() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(user("owner@test.com").roles("SUPER_ADMIN"))
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(4))
                .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void listarUsuariosDebeRestringirseASuperAdmin() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(user("admin@test.com").roles("ADMIN_INSTITUCION")))
                .andExpect(status().isForbidden());
    }

    @Test
    void superAdminDebePoderEditarUsuarios() throws Exception {
        Usuario usuario = usuarioRepository.findByEmail("ana.admin@test.com").orElseThrow();

        mockMvc.perform(put("/usuarios/{id}", usuario.getId())
                        .with(user("owner@test.com").roles("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana Admin Editada",
                                  "email": "ana.admin.editada@test.com",
                                  "rol": "ADMIN_INSTITUCION",
                                  "institucionId": %d,
                                  "estado": "ACTIVO"
                                }
                                """.formatted(institucionSur.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario actualizado"))
                .andExpect(jsonPath("$.data.nombre").value("Ana Admin Editada"))
                .andExpect(jsonPath("$.data.email").value("ana.admin.editada@test.com"))
                .andExpect(jsonPath("$.data.rol").value("ADMIN_INSTITUCION"))
                .andExpect(jsonPath("$.data.institucionId").value(institucionSur.getId()))
                .andExpect(jsonPath("$.data.institucionNombre").value("Institucion Sur"));
    }

    private Rol ensureRole(RolNombre nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rol.setDescripcion("Rol " + nombre.name());
            return rolRepository.save(rol);
        });
    }

    private Institucion createInstitution(String codigo, String nombre) {
        Institucion institucion = new Institucion();
        institucion.setCodigo(codigo);
        institucion.setNombre(nombre);
        return institucionRepository.save(institucion);
    }

    private Usuario createUser(String nombre, String email, Rol rol, Institucion institucion) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword("encoded-password");
        usuario.setRol(rol);
        usuario.setInstitucion(institucion);
        return usuarioRepository.save(usuario);
    }
}

