package com.corporacion.tecnica.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Programa;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.entity.Semestre;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.repository.ProgramaRepository;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.SedeRepository;
import com.corporacion.tecnica.repository.SemestreRepository;
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
class SemestreControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SemestreRepository semestreRepository;
    @Autowired
    private ProgramaRepository programaRepository;
    @Autowired
    private InstitucionRepository institucionRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Institucion institucion;
    private Sede sede;
    private Programa programa;

    @BeforeEach
    void setUp() {
        Rol adminSedeRole = ensureRole(RolNombre.ADMIN_SEDE);
        Rol adminInstRole = ensureRole(RolNombre.ADMIN_INSTITUCION);
        Rol docenteRole = ensureRole(RolNombre.DOCENTE);

        institucion = createInstitution("INST-SEM", "Institucion Semestres");
        sede = createSede("Sede Central", institucion);

        createUser("Admin Sede", "admin.sede@test.com", adminSedeRole, institucion, sede);
        createUser("Admin Inst", "admin.inst@test.com", adminInstRole, institucion, null);
        createUser("Docente", "docente@test.com", docenteRole, institucion, sede);

        programa = new Programa();
        programa.setNombre("Sistemas");
        programa.setDuracionSemestres(6);
        programa.setNivel("Tecnico");
        programa.setCostoSemestral(new BigDecimal("1200000.00"));
        programa.setInstitucion(institucion);
        programa.setSede(sede);
        programa = programaRepository.save(programa);

        Semestre semestre1 = new Semestre();
        semestre1.setNumero(1);
        semestre1.setNombre("Semestre 1");
        semestre1.setAnio(2026);
        semestre1.setPrograma(programa);
        semestre1.setInstitucion(institucion);
        semestreRepository.save(semestre1);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Test
    void adminSedeDebePoderCrearSemestre() throws Exception {
        mockMvc.perform(post("/semestres")
                        .with(user("admin.sede@test.com").roles("ADMIN_SEDE"))
                        .header("X-Institucion-Id", institucion.getId())
                        .header("X-Sede-Id", sede.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "bb",
                                  "numero": 2,
                                  "anio": 2026,
                                  "estado": "ACTIVO",
                                  "programaId": %d,
                                  "institucionId": %d,
                                  "sedeId": %d
                                }
                                """.formatted(programa.getId(), institucion.getId(), sede.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Semestre creado"))
                .andExpect(jsonPath("$.data.nombre").value("bb"))
                .andExpect(jsonPath("$.data.numero").value(2))
                .andExpect(jsonPath("$.data.anio").value(2026))
                .andExpect(jsonPath("$.data.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.data.programaId").value(programa.getId()))
                .andExpect(jsonPath("$.data.programaNombre").value("Sistemas"))
                .andExpect(jsonPath("$.data.institucionId").value(institucion.getId()))
                .andExpect(jsonPath("$.data.institucionNombre").value("Institucion Semestres"));
    }

    @Test
    void adminSedeDebePoderCrearSemestreSinInstitucionIdExplicito() throws Exception {
        mockMvc.perform(post("/semestres")
                        .with(user("admin.sede@test.com").roles("ADMIN_SEDE"))
                        .header("X-Institucion-Id", institucion.getId())
                        .header("X-Sede-Id", sede.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Semestre Dos",
                                  "numero": 2,
                                  "programaId": %d
                                }
                                """.formatted(programa.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Semestre creado"))
                .andExpect(jsonPath("$.data.nombre").value("Semestre Dos"))
                .andExpect(jsonPath("$.data.numero").value(2))
                .andExpect(jsonPath("$.data.programaId").value(programa.getId()));
    }

    @Test
    void adminSedeDebePoderListarSemestres() throws Exception {
        mockMvc.perform(get("/semestres")
                        .with(user("admin.sede@test.com").roles("ADMIN_SEDE"))
                        .header("X-Institucion-Id", institucion.getId())
                        .header("X-Sede-Id", sede.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Listado de semestres"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].nombre").value("Semestre 1"))
                .andExpect(jsonPath("$.data.content[0].anio").value(2026))
                .andExpect(jsonPath("$.data.content[0].estado").value("ACTIVO"))
                .andExpect(jsonPath("$.data.content[0].programaNombre").value("Sistemas"))
                .andExpect(jsonPath("$.data.content[0].institucionNombre").value("Institucion Semestres"));
    }

    @Test
    void adminInstitucionDebePoderCrearSemestre() throws Exception {
        mockMvc.perform(post("/semestres")
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucion.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Semestre 3",
                                  "numero": 3,
                                  "programaId": %d,
                                  "institucionId": %d
                                }
                                """.formatted(programa.getId(), institucion.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Semestre creado"))
                .andExpect(jsonPath("$.data.numero").value(3));
    }

    @Test
    void estudianteNoDebePoderCrearSemestre() throws Exception {
        mockMvc.perform(post("/semestres")
                        .with(user("estudiante@test.com").roles("ESTUDIANTE"))
                        .header("X-Institucion-Id", institucion.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Semestre 4",
                                  "numero": 4,
                                  "programaId": %d
                                }
                                """.formatted(programa.getId())))
                .andExpect(status().isForbidden());
    }

    private Rol ensureRole(RolNombre nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rol.setDescripcion("Rol " + nombre);
            return rolRepository.save(rol);
        });
    }

    private Institucion createInstitution(String codigo, String nombre) {
        Institucion inst = new Institucion();
        inst.setCodigo(codigo);
        inst.setNombre(nombre);
        return institucionRepository.save(inst);
    }

    private Sede createSede(String nombre, Institucion inst) {
        Sede s = new Sede();
        s.setNombre(nombre);
        s.setDireccion("Avenida 123");
        s.setCiudad("Bogota");
        s.setInstitucion(inst);
        return sedeRepository.save(s);
    }

    private Usuario createUser(String nombre, String email, Rol rol, Institucion inst, Sede s) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword("encoded");
        usuario.setRol(rol);
        usuario.setInstitucion(inst);
        usuario.setSede(s);
        return usuarioRepository.save(usuario);
    }
}
