package com.corporacion.tecnica.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Materia;
import com.corporacion.tecnica.entity.Programa;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.entity.Semestre;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
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
class MateriaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MateriaRepository materiaRepository;
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
    private Sede sede1;
    private Sede sede2;
    private Programa programaSede1;
    private Programa programaSede2;
    private Semestre semestreSede1;
    private Semestre semestreSede2;
    private Materia materiaSede1;
    private Materia materiaSede2;

    @BeforeEach
    void setUp() {
        Rol adminSedeRole = ensureRole(RolNombre.ADMIN_SEDE);
        Rol adminInstRole = ensureRole(RolNombre.ADMIN_INSTITUCION);
        Rol docenteRole = ensureRole(RolNombre.DOCENTE);

        institucion = createInstitution("INST-MAT", "Institucion Materias");
        sede1 = createSede("Sede Norte", institucion);
        sede2 = createSede("Sede Sur", institucion);

        createUser("Admin Sede 1", "admin.sede1@test.com", adminSedeRole, institucion, sede1);
        createUser("Admin Sede 2", "admin.sede2@test.com", adminSedeRole, institucion, sede2);
        createUser("Admin Inst", "admin.inst@test.com", adminInstRole, institucion, null);
        createUser("Docente", "docente@test.com", docenteRole, institucion, sede1);

        programaSede1 = createPrograma("Ingenieria de Software", institucion, sede1);
        programaSede2 = createPrograma("Administracion", institucion, sede2);

        semestreSede1 = createSemestre("Semestre 1 - Software", 1, programaSede1, institucion);
        semestreSede2 = createSemestre("Semestre 1 - Admin", 1, programaSede2, institucion);

        materiaSede1 = createMateria("Programacion I", 64, semestreSede1, institucion);
        materiaSede2 = createMateria("Contabilidad Basica", 48, semestreSede2, institucion);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Test
    void adminSedeDebeListarSoloMateriasDeSuSede() throws Exception {
        mockMvc.perform(get("/materias")
                        .with(user("admin.sede1@test.com").roles("ADMIN_SEDE"))
                        .header("X-Institucion-Id", institucion.getId())
                        .header("X-Sede-Id", sede1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Listado de materias"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].nombre").value("Programacion I"))
                .andExpect(jsonPath("$.data.content[0].semestreId").value(semestreSede1.getId()))
                .andExpect(jsonPath("$.data.content[0].programaId").value(programaSede1.getId()))
                .andExpect(jsonPath("$.data.content[0].institucionId").value(institucion.getId()));
    }

    @Test
    void adminInstitucionDebeListarTodasLasMateriasDeLaInstitucion() throws Exception {
        mockMvc.perform(get("/materias")
                        .with(user("admin.inst@test.com").roles("ADMIN_INSTITUCION"))
                        .header("X-Institucion-Id", institucion.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Listado de materias"))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void adminSedeDebePoderCrearMateriaEnSuSede() throws Exception {
        mockMvc.perform(post("/materias")
                        .with(user("admin.sede1@test.com").roles("ADMIN_SEDE"))
                        .header("X-Institucion-Id", institucion.getId())
                        .header("X-Sede-Id", sede1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Bases de Datos I",
                                  "intensidadHoraria": 48,
                                  "semestreId": %d
                                }
                                """.formatted(semestreSede1.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Materia creada"))
                .andExpect(jsonPath("$.data.nombre").value("Bases de Datos I"))
                .andExpect(jsonPath("$.data.intensidadHoraria").value(48))
                .andExpect(jsonPath("$.data.semestreId").value(semestreSede1.getId()))
                .andExpect(jsonPath("$.data.programaId").value(programaSede1.getId()))
                .andExpect(jsonPath("$.data.institucionId").value(institucion.getId()));
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

    private Programa createPrograma(String nombre, Institucion inst, Sede s) {
        Programa prog = new Programa();
        prog.setNombre(nombre);
        prog.setDuracionSemestres(6);
        prog.setNivel("Tecnico");
        prog.setCostoSemestral(new BigDecimal("1200000.00"));
        prog.setInstitucion(inst);
        prog.setSede(s);
        return programaRepository.save(prog);
    }

    private Semestre createSemestre(String nombre, int numero, Programa programa, Institucion inst) {
        Semestre sem = new Semestre();
        sem.setNombre(nombre);
        sem.setNumero(numero);
        sem.setAnio(2026);
        sem.setPrograma(programa);
        sem.setInstitucion(inst);
        return semestreRepository.save(sem);
    }

    private Materia createMateria(String nombre, int intensidad, Semestre sem, Institucion inst) {
        Materia mat = new Materia();
        mat.setNombre(nombre);
        mat.setIntensidadHoraria(intensidad);
        mat.setSemestre(sem);
        mat.setInstitucion(inst);
        return materiaRepository.save(mat);
    }
}
