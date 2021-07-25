package com.webflux.thiago.webfluxdevdojo.config;

import com.webflux.thiago.webfluxdevdojo.domain.ApplicationUser;
import com.webflux.thiago.webfluxdevdojo.service.ApplicationUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET, "/animes/**").hasRole("USER")
                .pathMatchers(HttpMethod.POST, "/animes/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/animes/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/animes/**").hasRole("ADMIN")
                .pathMatchers("/webjars/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and().build();
    }

    @Bean
    ReactiveAuthenticationManager authenticationManager(ApplicationUserService applicationUser){
        return new UserDetailsRepositoryReactiveAuthenticationManager(applicationUser);
    }
//    @Bean
//    public MapReactiveUserDetailsService mapReactiveUserDetailsService(){
//        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder.encode("devdojo"))
//                .roles("USER")
//                .build();
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder.encode("devdojo"))
//                .roles("USER", "ADMIN")
//                .build();
//                return new MapReactiveUserDetailsService(user, admin);
//    }

}
