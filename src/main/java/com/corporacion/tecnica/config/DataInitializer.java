package com.corporacion.tecnica.config;

import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RolRepository rolRepository;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    CommandLineRunner initRoles() {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE roles MODIFY COLUMN nombre VARCHAR(50) NOT NULL");
            } catch (Exception e) {
                log.debug("No se pudo ejecutar ALTER TABLE roles MODIFY COLUMN nombre (puede no ser necesario o no ser MySQL): {}", e.getMessage());
            }

            for (RolNombre rolNombre : RolNombre.values()) {
                rolRepository.findByNombre(rolNombre).orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setNombre(rolNombre);
                    rol.setDescripcion("Rol " + rolNombre.name());
                    return rolRepository.save(rol);
                });
            }
        };
    }
}

