package com.webflux.thiago.webfluxdevdojo.repository;

import com.webflux.thiago.webfluxdevdojo.domain.ApplicationUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ApplicationUserRepository extends ReactiveCrudRepository<ApplicationUser, Integer>{
    Mono<ApplicationUser> findByUsername(String username);
}
