package com.pies.api.projeto.integrado.pies_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pies.api.projeto.integrado.pies_backend.model.User;
import com.pies.api.projeto.integrado.pies_backend.repository.UserRepository;

@Service // Indica que é um serviço gerenciado pelo Spring
public class AuthorizationService implements UserDetailsService { // Implementa interface do Spring Security para autenticação

    @Autowired
    UserRepository repository; // Repositório para buscar usuários no banco de dados
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Método obrigatório do Spring Security para carregar usuário pelo nome de usuário (email)
        // Este método é chamado automaticamente pelo Spring Security durante a autenticação
        
        // Busca o usuário no banco de dados pelo email
        User user = repository.findByEmail(username);
        
        // Se o usuário não for encontrado, lança uma exceção
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        // Retorna o usuário encontrado (que implementa UserDetails)
        return user;
    }
}