package com.pnae.infrastructure.config;

import com.pnae.domain.model.Role;
import com.pnae.domain.model.Usuario;
import com.pnae.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("admin@pnae.gov.br")) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador")
                    .email("admin@pnae.gov.br")
                    .senha(passwordEncoder.encode("Admin@2024!"))
                    .role(Role.ADMIN)
                    .build();
            usuarioRepository.save(admin);
            log.info("Usuário admin criado: admin@pnae.gov.br");
        }
    }
}
