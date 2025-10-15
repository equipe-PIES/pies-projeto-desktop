package com.pies.api.projeto.integrado.pies_backend.controller;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.AuthenticationDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.RegisterDTO;
import com.pies.api.projeto.integrado.pies_backend.model.User;
import com.pies.api.projeto.integrado.pies_backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController //indicando que é um controller rest
@RequestMapping("auth")//mapeando o endpoint na qual esse controller vai ser chamado, se chamar auth vai cair nesse endpoint

public class AuthenticationController {//endpoint pro usuario fazer login
    @Autowired
    private AuthenticationManager authenticationManager; //

    @Autowired
    private UserRepository repository;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){

        var usernamepassword = new UsernamePasswordAuthenticationToken(data.login(),data.password());
        var auth = this.authenticationManager.authenticate(usernamepassword);//recebe o login e a senha juntos formados como um token

        return ResponseEntity.ok().build();

    }
    //novos usuarios

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        //verificar se o login existe no banco de dados, caso n exista fazemos o cadastro desse novo usuario
        if(this.repository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password()); //pegando o hash da senha do usuario

        User newUser = new User(data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();

    }

}
