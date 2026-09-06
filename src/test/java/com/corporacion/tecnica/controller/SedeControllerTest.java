package com.corporacion.tecnica.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.SedeRepository;
import com.corporacion.tecnica.repository.UsuarioRepository;
import com.corporacion.tecnica.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SedeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private InstitucionRepository institucionRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private SedeRepository sedeRepository;

    private Institucion institucion;
    private Sede sede;

    @BeforeEach
    void setUp() {
        Rol adminInstRole = ensureRole(RolNombre.ADMIN_INSTITUCION);
        Rol adminSedeRole = ensureRole(RolNombre.ADMIN_SEDE);

        institucion = new Institucion();
        institucion.setCodigo("INST-SEDE");
        institucion.setNombre("Institucion Sedes");
        institucion = institucionRepository.save(institucion);

        createUser("Admin Inst", "admin.inst@test.com", adminInstRole, institucion, null);
        createUser("Admin Sede", "admin.sede@test.com", adminSedeRole, institucion, null);

        sede = new Sede();
        sede.setNombre("Sede Centro");
        sede.setCiudad("Bogota");
        sede.setDireccion("Cra 1");
        sede.setInstitucion(institucion);
        sede = sedeRepository.save(sede);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Test
    void adminInstitucionDebeCrearSede() throws Exception {
        mockMvc.perform(post("/sedes")
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucion.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Sede Norte",
                                  "ciudad": "Medellin",
                                  "direccion": "Calle 100",
                                  "institucionId": %d,
                                  "estado": "ACTIVO"
                                }
                                """.formatted(institucion.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Sede creada"))
                .andExpect(jsonPath("$.data.nombre").value("Sede Norte"))
                .andExpect(jsonPath("$.data.institucionId").value(institucion.getId()));
    }

    @Test
    void adminInstitucionDebeListarSedesConFiltro() throws Exception {
        mockMvc.perform(get("/sedes")
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucion.getId())
                        .param("nombre", "centro")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Listado de sedes"))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].nombre").value("Sede Centro"));
    }

    @Test
    void adminInstitucionDebeActualizarSede() throws Exception {
        mockMvc.perform(put("/sedes/{id}", sede.getId())
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucion.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Sede Centro Actualizada",
                                  "ciudad": "Bogota",
                                  "direccion": "Calle 200",
                                  "institucionId": %d,
                                  "estado": "INACTIVO"
                                }
                                """.formatted(institucion.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sede actualizada"))
                .andExpect(jsonPath("$.data.nombre").value("Sede Centro Actualizada"))
                .andExpect(jsonPath("$.data.estado").value("INACTIVO"));
    }

    @Test
    void adminSedeNoDebeGestionarSedes() throws Exception {
        mockMvc.perform(get("/sedes")
                        .with(user("admin.sede@test.com").roles("ADMIN_SEDE"))
                        .header("X-Institucion-Id", institucion.getId()))
                .andExpect(status().isForbidden());
    }

    private Rol ensureRole(RolNombre nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rol.setDescripcion("Rol " + nombre.name());
            return rolRepository.save(rol);
        });
    }

    private Usuario createUser(String nombre, String email, Rol rol, Institucion institucion, Sede sede) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword("encoded-password");
        usuario.setRol(rol);
        usuario.setInstitucion(institucion);
        usuario.setSede(sede);
        return usuarioRepository.save(usuario);
    }
}

