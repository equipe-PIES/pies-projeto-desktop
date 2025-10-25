package com.pies.api.projeto.integrado.pies_backend.infra.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pies.api.projeto.integrado.pies_backend.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService; // Serviço para validar tokens JWT

    @Autowired
    private UserRepository userRepository; // Repositório para buscar usuários no banco

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extrai o token JWT do header Authorization da requisição
        var tokenJWT = recuperarToken(request);

        // Se existe um token na requisição
        if (tokenJWT != null) {
            // Valida o token e extrai o email do usuário (subject)
            var subject = tokenService.validateToken(tokenJWT);
            if (subject != null) {
                // Busca o usuário no banco de dados pelo email
                UserDetails user = userRepository.findByEmail(subject);
                if (user != null) {
                    // Cria um objeto de autenticação com o usuário e suas permissões
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    // Define o usuário como autenticado no contexto de segurança do Spring
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continua o processamento da requisição (passa para o próximo filtro/controller)
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        // Pega o header "Authorization" da requisição
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            // Remove o prefixo "Bearer " e retorna apenas o token
            // Exemplo: "Bearer eyJhbGciOiJIUzI1NiIs..." -> "eyJhbGciOiJIUzI1NiIs..."
            return authorizationHeader.replace("Bearer ", "");
        }
        return null; // Retorna null se não houver token
    }
}
