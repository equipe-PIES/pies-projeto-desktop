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

/**
 * Filtro de segurança que intercepta todas as requisições para validar o token JWT
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        System.out.println("=== SECURITY FILTER DEBUG ===");
        System.out.println("Request URI: " + requestURI);
        System.out.println("Request Method: " + request.getMethod());
        
        // Permite rotas públicas sem autenticação
        if (requestURI.equals("/auth/login") || requestURI.equals("/auth/register")) {
            System.out.println("Rota pública - passando sem autenticação");
            filterChain.doFilter(request, response);
            return;
        }
        
        var token = this.recoverToken(request);
        System.out.println("Token presente: " + (token != null));
        
        if (token != null) {
            var login = tokenService.validateToken(token);
            System.out.println("Login do token: " + login);
            UserDetails user = userRepository.findByEmail(login);
            System.out.println("User encontrado: " + (user != null));

            if (user != null) {
                System.out.println("Authorities do user: " + user.getAuthorities());
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication configurado!");
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Recupera o token JWT do header Authorization
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
