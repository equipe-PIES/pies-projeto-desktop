package com.pies.api.projeto.integrado.pies_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pies.api.projeto.integrado.pies_backend.model.User;


public interface UserRepository extends JpaRepository <User, String>{
    //metodo para consultar usuario pelo email
    User findByEmail(String email);//retorna um user para ser usado pela spring security
}


