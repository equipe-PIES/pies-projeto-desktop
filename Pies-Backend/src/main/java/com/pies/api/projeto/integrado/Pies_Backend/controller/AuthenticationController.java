package com.pies.api.projeto.integrado.Pies_Backend.controller;

import com.pies.api.projeto.integrado.Pies_Backend.model.AuthenticationDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController //indicando que é um controller rest
@RequestMapping("auth")//mapeando o endpoint na qual esse controller vai ser chamado, se chamar auth vai cair nesse endpoint
public class AuthenticationController {//endpoint pro usuario fazer login
    @Autowired
    private AuthenticationManager authenticationManager; //


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamepassword = new UsernamePasswordAuthenticationToken(data.login(),data.password());
        var auth = this.authenticationManager.authenticate(usernamepassword);//recebe o login e a senha juntos formados como um token
        return ResponseEntity.ok().build();
    }
}
