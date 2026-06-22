package com.pnae.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/registrar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/auth/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/cardapios/*/aprovar").hasAnyRole("ADMIN", "DIRETOR")
                .requestMatchers(HttpMethod.PATCH, "/api/cardapios/*/rejeitar").hasAnyRole("ADMIN", "DIRETOR")
                .requestMatchers(HttpMethod.POST, "/api/estoque/**").hasAnyRole("ADMIN", "NUTRICIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/alimentos/**").hasAnyRole("ADMIN", "NUTRICIONISTA")
                .requestMatchers(HttpMethod.PUT, "/api/alimentos/**").hasAnyRole("ADMIN", "NUTRICIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/cardapios/**").hasAnyRole("ADMIN", "NUTRICIONISTA")
                .requestMatchers(HttpMethod.PUT, "/api/cardapios/**").hasAnyRole("ADMIN", "NUTRICIONISTA")
                .requestMatchers(HttpMethod.PATCH, "/api/cardapios/**").hasAnyRole("ADMIN", "NUTRICIONISTA")
                .requestMatchers("/api/fornecedores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/estoque/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/relatorios/**").hasAnyRole("ADMIN", "DIRETOR", "NUTRICIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/relatorios/**").hasAnyRole("ADMIN", "DIRETOR", "NUTRICIONISTA")
                .requestMatchers(HttpMethod.GET, "/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Autenticação necessária\",\"path\":\"%s\"}"
                                .formatted(request.getRequestURI()));
                })
                .accessDeniedHandler((request, response, e) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Acesso negado\",\"path\":\"%s\"}"
                                .formatted(request.getRequestURI()));
                })
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
