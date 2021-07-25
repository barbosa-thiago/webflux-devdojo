package com.webflux.thiago.webfluxdevdojo.service;

import com.webflux.thiago.webfluxdevdojo.domain.Anime;
import com.webflux.thiago.webfluxdevdojo.repository.AnimeRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository animeRepository;

    public Flux<Anime> findAll(){
        return animeRepository.findAll();
    }
    public Mono<Anime> findById(int id){
        return animeRepository.findById(id)
                .switchIfEmpty(responseStatusNotFoundException());
    }

    public <T> Mono<T> responseStatusNotFoundException(){
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
    }

    public Mono<Anime> save(Anime anime){
        return animeRepository.save(anime);
    }

    public Mono<Void> update(Anime anime){
        return findById(anime.getId())
                .flatMap(animeFound -> animeRepository.save(anime.withId(animeFound.getId())))
                .then();
    }

    public Mono<Void> delete(int id){
        return findById(id)
                .flatMap(animeRepository::delete);
    }


    public Flux<Anime> saveAll(List<Anime> animes) {
        return animeRepository.saveAll(animes)
                .doOnNext(this::throwResponseStatusExceptionWhenEmptyName);
    }

    public void throwResponseStatusExceptionWhenEmptyName(Anime anime){
        if(StringUtil.isNullOrEmpty(anime.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
        }
    }
}
