package tech.fiap.hackaton.internal.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void testSecurityFilterChainBean() throws Exception {
        // Arrange
        HttpSecurity http = mock(HttpSecurity.class);

        // Act
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(http);

        // Assert
        assertNotNull(filterChain);
    }

    @Test
    void testPasswordEncoderBean() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder.matches("password", passwordEncoder.encode("password")));
    }

    @Test
    void testSecurityConfigAnnotations() {
        // Verifica se as anotações necessárias estão presentes
        assertNotNull(SecurityConfig.class.getAnnotation(Configuration.class));
        assertNotNull(SecurityConfig.class.getAnnotation(EnableWebSecurity.class));
        assertNotNull(SecurityConfig.class.getAnnotation(EnableMethodSecurity.class));
    }
}