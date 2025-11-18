package com.pies.api.projeto.integrado.pies_backend.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {// criacao do usuario
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(String email, String password, UserRole role){
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("=== DEBUG getAuthorities ===");
        System.out.println("User: " + this.email);
        System.out.println("Role enum: " + this.role);
        System.out.println("Role name: " + (this.role != null ? this.role.name() : "NULL"));
        
        if(this.role == UserRole.ADMIN) {
            System.out.println("Authorities: ROLE_ADMIN, ROLE_USER");
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else if(this.role == UserRole.PROFESSOR) {
            System.out.println("Authorities: ROLE_PROFESSOR");
            return List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
        } else if(this.role == UserRole.COORDENADOR) {
            System.out.println("Authorities: ROLE_COORDENADOR");
            return List.of(new SimpleGrantedAuthority("ROLE_COORDENADOR"));
        } else {
            System.out.println("Authorities: ROLE_USER (default)");
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
