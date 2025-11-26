package com.pies.api.projeto.integrado.pies_backend.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.UserRole;
import com.pies.api.projeto.integrado.pies_backend.model.User;
import com.pies.api.projeto.integrado.pies_backend.repository.UserRepository;

/**
 * Inicializador de dados para popular o banco H2 com usuários padrão
 * TEMPORARIAMENTE DESABILITADO - remova o comentário para reativar
 */
// @Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existem usuários no banco
        if (userRepository.count() > 0) {
            System.out.println("✓ Banco já possui usuários cadastrados");
            return;
        }

        System.out.println("=== INICIALIZANDO DADOS DO BANCO ===");

        String encryptedPassword = new BCryptPasswordEncoder().encode("123456");

        // Cria usuário ADMIN
        User admin = new User();
        admin.setName("Administrador");
        admin.setEmail("admin@escola.com");
        admin.setPassword(encryptedPassword);
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
        System.out.println("✓ Admin criado: admin@escola.com / 123456");

        // Cria usuário COORDENADOR
        User coordenador = new User();
        coordenador.setName("Coordenador");
        coordenador.setEmail("coordenador@test.com");
        coordenador.setPassword(encryptedPassword);
        coordenador.setRole(UserRole.COORDENADOR);
        userRepository.save(coordenador);
        System.out.println("✓ Coordenador criado: coordenador@test.com / 123456");

        // Cria usuário PROFESSOR
        User professor = new User();
        professor.setName("Professor Teste");
        professor.setEmail("professor@test.com");
        professor.setPassword(encryptedPassword);
        professor.setRole(UserRole.PROFESSOR);
        userRepository.save(professor);
        System.out.println("✓ Professor criado: professor@test.com / 123456");

        System.out.println("=== DADOS INICIAIS CRIADOS COM SUCESSO ===");
        System.out.println("Todos os usuários têm senha: 123456");
    }
}
