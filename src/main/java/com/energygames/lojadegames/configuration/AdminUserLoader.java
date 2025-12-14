package com.energygames.lojadegames.configuration;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.enums.RoleEnum;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.UsuarioRepository;

@Configuration
public class AdminUserLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserLoader.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        String adminEmail = "admin@energygames.com";
        
        if (usuarioRepository.findByUsuario(adminEmail).isEmpty()) {
            log.info("Usuário admin não encontrado. Criando usuário admin padrão...");
            
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setUsuario(adminEmail);
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setFoto("https://i.imgur.com/Sk5SjWE.jpeg"); // Foto placeholder
            
            // Adiciona a role de ADMIN
            // Nota: Dependendo de como o Set funciona na entidade, pode ser necessário inicializar ou usar add
            // Na entidade Usuario, roles é inicializado com new HashSet<>()
            admin.getRoles().add(RoleEnum.ROLE_ADMIN);
            
            // Opcional: Adicionar ROLE_USER também se necessário para funcionalidades básicas
            admin.getRoles().add(RoleEnum.ROLE_USER);
            
            usuarioRepository.save(admin);
            
            log.info("Usuário admin criado com sucesso: {}", adminEmail);
            log.info("Senha padrão: admin123");
        } else {
            log.info("Usuário admin já existe.");
        }
    }
}
