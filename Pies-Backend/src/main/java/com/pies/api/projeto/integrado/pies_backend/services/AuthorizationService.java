package com.pies.api.projeto.integrado.pies_backend.services;

import com.pies.api.projeto.integrado.pies_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {//metodo para o spring consultar os usuarios no banco de dados da tabela que foi criada
        return repository.findByLogin(username);
    }
}
