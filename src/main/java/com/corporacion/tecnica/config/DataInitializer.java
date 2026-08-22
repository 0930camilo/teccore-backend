package com.corporacion.tecnica.config;

import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RolRepository rolRepository;

    @Bean
    CommandLineRunner initRoles() {
        return args -> {
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

