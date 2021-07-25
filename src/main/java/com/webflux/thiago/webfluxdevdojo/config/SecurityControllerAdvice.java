package com.webflux.thiago.webfluxdevdojo.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

@ControllerAdvice
public class SecurityControllerAdvice {
    @ModelAttribute
    Mono<CsrfToken> csrfToken(ServerWebExchange serverWebExchange){
        Mono<CsrfToken> csrfToken = serverWebExchange.getAttribute(CsrfToken.class.getName());
        if(csrfToken == null){
            return Mono.empty();
        }
        return csrfToken.doOnSuccess(token -> serverWebExchange.getAttributes()
        .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }
    @ModelAttribute("currentUser")
    Mono<Principal> currentUser(Mono<Principal> currentUser){
        return currentUser;
    }
}
