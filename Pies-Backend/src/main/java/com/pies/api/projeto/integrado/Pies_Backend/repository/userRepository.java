package com.pies.api.projeto.integrado.Pies_Backend.repository;

import com.pies.api.projeto.integrado.Pies_Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepository extends JpaRepository<User, String> {

}
