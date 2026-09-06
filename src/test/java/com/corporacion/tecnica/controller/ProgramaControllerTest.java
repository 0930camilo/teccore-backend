package com.corporacion.tecnica.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.Programa;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.repository.ProgramaRepository;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.UsuarioRepository;
import com.corporacion.tecnica.util.TenantContext;
import java.math.BigDecimal;
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
class ProgramaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProgramaRepository programaRepository;
    @Autowired
    private InstitucionRepository institucionRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Institucion institucionNorte;
    private Programa programa;

    @BeforeEach
    void setUp() {
        Rol adminRole = ensureRole(RolNombre.ADMIN_INSTITUCION);
        Rol docenteRole = ensureRole(RolNombre.DOCENTE);

        institucionNorte = createInstitution("INST-PROG", "Institucion Programas");
        createUser("Admin Inst", "admin.inst@test.com", adminRole, institucionNorte);
        createUser("Docente Inst", "docente.inst@test.com", docenteRole, institucionNorte);

        programa = new Programa();
        programa.setNombre("Ingenieria de Software");
        programa.setDuracionSemestres(10);
        programa.setNivel("Profesional");
        programa.setCostoSemestral(new BigDecimal("1500000.00"));
        programa.setInstitucion(institucionNorte);
        programa = programaRepository.save(programa);

        Programa programaSecundario = new Programa();
        programaSecundario.setNombre("Administracion");
        programaSecundario.setDuracionSemestres(8);
        programaSecundario.setNivel("Tecnico");
        programaSecundario.setCostoSemestral(new BigDecimal("900000.00"));
        programaSecundario.setEstado(EstadoRegistro.INACTIVO);
        programaSecundario.setInstitucion(institucionNorte);
        programaRepository.save(programaSecundario);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Test
    void listarProgramasDebeIncluirCostoSemestral() throws Exception {
        mockMvc.perform(get("/programas")
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucionNorte.getId())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Listado de programas"))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].estado").exists());
    }

    @Test
    void listarProgramasDebeFiltrarPorNombre() throws Exception {
        mockMvc.perform(get("/programas")
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucionNorte.getId())
                        .param("nombre", "software")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Listado de programas"))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].nombre").value("Ingenieria de Software"))
                .andExpect(jsonPath("$.data.content[0].estado").value("ACTIVO"))
                .andExpect(jsonPath("$.data.content[0].costoSemestral").value(1500000.00));
    }

    @Test
    void listarProgramasDebeFiltrarPorEstado() throws Exception {
        mockMvc.perform(get("/programas")
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucionNorte.getId())
                        .param("estado", "INACTIVO")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Listado de programas"))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].nombre").value("Administracion"))
                .andExpect(jsonPath("$.data.content[0].estado").value("INACTIVO"));
    }

    @Test
    void adminInstitucionDebePoderEditarProgramaConCostoSemestral() throws Exception {
        mockMvc.perform(put("/programas/{id}", programa.getId())
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucionNorte.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ingenieria de Sistemas",
                                  "duracionSemestres": 9,
                                  "nivel": "Universitario",
                                  "costoSemestral": 1750000.50,
                                  "estado": "INACTIVO",
                                  "institucionId": %d
                                }
                                """.formatted(institucionNorte.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Programa actualizado"))
                .andExpect(jsonPath("$.data.nombre").value("Ingenieria de Sistemas"))
                .andExpect(jsonPath("$.data.duracionSemestres").value(9))
                .andExpect(jsonPath("$.data.nivel").value("Universitario"))
                .andExpect(jsonPath("$.data.estado").value("INACTIVO"))
                .andExpect(jsonPath("$.data.costoSemestral").value(1750000.50));
    }

    @Test
    void docenteNoDebePoderEditarPrograma() throws Exception {
        mockMvc.perform(put("/programas/{id}", programa.getId())
                        .with(user("docente.inst@test.com").roles("DOCENTE"))
                        .header("X-Institucion-Id", institucionNorte.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Programa Bloqueado",
                                  "duracionSemestres": 8,
                                  "nivel": "Tecnico",
                                  "costoSemestral": 900000,
                                  "institucionId": %d
                                }
                                """.formatted(institucionNorte.getId())))
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



