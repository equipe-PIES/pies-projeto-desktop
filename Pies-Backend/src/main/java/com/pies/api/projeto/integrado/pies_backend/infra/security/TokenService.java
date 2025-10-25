package com.pies.api.projeto.integrado.pies_backend.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.pies.api.projeto.integrado.pies_backend.model.User;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String gererateToken(User user){//função de geração de tokek
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create() //gerando o token
                    .withIssuer("login-auth-api") //quem emite o token e o microserviço
                    .withSubject(user.getEmail()) //quem é o sujeito que ta ganhando o token
                    .withExpiresAt(this.generateExpirationDate()) //função que gera a hora que expira o token
                    .sign(algorithm); //passando o algoritmo para gerar o token
            return token;
        } catch (JWTCreationException exception){
            throw new RuntimeException("Error while authenticating");
        }
    }
    //validar o token
    public String validateToken(String token){
        try {//se eu conseguir validar
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")//montando o obj par fazer a verif
                    .build()//montando o obj par fazer a verif
                    .verify(token)//verificando, caso dê problema na verif ele lança a excessão do catch retornando nulo
                    .getSubject();//pegando o valor que foi salvo no token no momento da geração
        } catch (JWTVerificationException exception){//caso dê erro de validação retorna nulo por que tá so validando token
            return null; //se vier como nulo o usuario não foi autentiocado, por erro no email ou etc
        }
    }
    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
