package com.webflux.thiago.webfluxdevdojo.service;


import com.webflux.thiago.webfluxdevdojo.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApplicationUserService implements ReactiveUserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return applicationUserRepository.findByUsername(username)
                .cast(UserDetails.class);
    }
}
