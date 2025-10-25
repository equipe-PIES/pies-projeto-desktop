package com.pies.api.projeto.integrado.pies_backend.infra.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration //indicando pro sprint que é uma classe de config
@EnableWebSecurity //habilitando no web security para eu conseguir configurar dentro da classe
public class SecurityConfigurations {
    @Bean // pro spring conseguir instanciar a classe
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, SecurityFilter securityFilter) throws Exception {
        return httpSecurity //configurar no httpSecurity
                .csrf(AbstractHttpConfigurer::disable)//desligando esse config
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()//permite que qualquer pessoa faça requisição pra endpoint
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()//permite registro de novos usuários
                        .requestMatchers(HttpMethod.POST, "/product").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean// pro spring fazer a injeção correta e ele seja ultilizado
    public PasswordEncoder passwordEncoder(){//classe do spring security p fzr criptografia das senha
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Cria configuração CORS para permitir requisições cross-origin
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permite requisições de qualquer origem (* = todos os domínios)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Define quais métodos HTTP são permitidos (GET, POST, PUT, DELETE, OPTIONS)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permite qualquer header nas requisições
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permite envio de cookies e credenciais (importante para JWT)
        configuration.setAllowCredentials(true);
        
        // Cria o gerenciador de configurações CORS
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Aplica essa configuração para todas as rotas (/**)
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}


