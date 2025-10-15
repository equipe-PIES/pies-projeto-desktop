package com.pies.api.projeto.integrado.pies_backend.repository;

import com.pies.api.projeto.integrado.pies_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserRepository extends JpaRepository <User, String>{
    //metodo para consultar usuario pelo login
    UserDetails findByLogin(String login);//retorna um user details para ser usado pela spring security


    }


