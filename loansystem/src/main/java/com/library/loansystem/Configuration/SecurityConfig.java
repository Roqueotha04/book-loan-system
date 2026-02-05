package com.library.loansystem.Configuration;

import com.library.loansystem.Configuration.Filters.JwtTokenValidator;
import com.library.loansystem.Utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class    SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity)throws Exception{
        return httpSecurity
                .csrf(csrf->csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/publishers/**", "/authors/**", "/books/**", "/book-copies/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/publishers/**", "/authors/**", "/books/**", "/book-copies/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/publishers/**", "/authors/**", "/books/**", "/book-copies/**").hasAnyRole("LIBRARIAN","ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/publishers/**", "/authors/**", "/books/**", "/book-copies/**").hasAnyRole("LIBRARIAN","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/publishers/**", "/authors/**", "/books/**", "/book-copies/**").permitAll()
                        /// Loan
                        .requestMatchers(HttpMethod.POST, "/loans").hasAnyRole("USER", "LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/loans/{id}/renew").hasAnyRole("USER", "LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/loans/user/**").hasAnyRole("USER", "LIBRARIAN", "ADMIN")
                        .requestMatchers("/loans/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        /// User
                        .requestMatchers(HttpMethod.PATCH, "/users/{id}/deactivate").hasAnyRole("USER", "LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users/{id}/activate").hasAnyRole("USER", "LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users/change-password").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("LIBRARIAN, ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasAnyRole("ADMIN")

                        .anyRequest().denyAll()
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }


}
