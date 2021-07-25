package com.webflux.thiago.webfluxdevdojo.controller;

import com.webflux.thiago.webfluxdevdojo.domain.Anime;
import com.webflux.thiago.webfluxdevdojo.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("animes")
@Slf4j
@SecurityScheme(name = "Basic Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "basic")
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "lidt all animes",
        security = @SecurityRequirement(name = "Basic Authentication"),
        tags = "anime")
    public Flux<Anime> findAll(){
        return animeService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "find one anime",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = "anime")
    public Mono<Anime> findById(@PathVariable int id){
        return animeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "create anime",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = "anime")
    public Mono<Anime> save(@Valid @RequestBody Anime anime){
        return animeService.save(anime);
    }

    @Transactional
    @PostMapping(path = "batch")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "create more than one anime",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = "anime")
    public Flux<Anime> saveBatch(@RequestBody List<Anime> animes){
        return animeService.saveAll(animes);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "update anime",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = "anime")
    public Mono<Void> update(@PathVariable int id, @Valid @RequestBody Anime anime){
        return animeService.update(anime.withId(id));
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "removes anime",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = "anime")
    public Mono<Void> delete(@PathVariable int id){
        return animeService.delete(id);
    }

}
