package com.webflux.thiago.webfluxdevdojo.controller;

import com.webflux.thiago.webfluxdevdojo.domain.Anime;
import com.webflux.thiago.webfluxdevdojo.service.AnimeService;
import com.webflux.thiago.webfluxdevdojo.util.AnimeCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeService animeServiceMock;

    Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    public static void blockHoundSetUp(){
        BlockHound.install(builder -> builder
                .allowBlockingCallsInside("java.util.UUID", "randomUUID"));
    }
    @BeforeEach
    public void setUp(){
        BDDMockito.when(animeServiceMock.findAll()).thenReturn(Flux.just(anime));

        BDDMockito.when(animeServiceMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));

        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(Anime.class))).thenReturn(Mono.just(anime));

        BDDMockito.when(animeServiceMock
                .saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
                .thenReturn(Flux.just(anime, anime));

        BDDMockito.when(animeServiceMock.delete(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());

        BDDMockito.when(animeServiceMock.update(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("FindAll list all animes when succesful")
    public void findAll_ListAnimes_WhenSuccesful(){
        StepVerifier.create(animeController.findAll())
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("FindById return anime mono when succesful")
    public void findById_ReturnAnimeMono_WhenSuccesful(){
        StepVerifier.create(animeController.findById(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("Save creates anime when succesful")
    public void save_CreatesAnime_WhenSuccesful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        StepVerifier.create(animeController.save(animeToBeSaved))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("SaveBatch creates list of anime when succesful")
    public void saveBatch_CreatesAnimeList_WhenSuccesful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        StepVerifier.create(animeController.saveBatch(List.of(animeToBeSaved, animeToBeSaved)))
                .expectSubscription()
                .expectNext(anime, anime)
                .verifyComplete();
    }


    @Test
    @DisplayName("Delete removes anime when succesful")
    public void delete_RemovesAnime_WhenSuccesful(){
        StepVerifier.create(animeServiceMock.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update save updated anime and return empty mono when succesful")
    public void update_SavesUpdatedAnime_WhenSuccesful(){

        StepVerifier.create(animeController.update(1, AnimeCreator.createAnimeToBeSaved()))
                .expectSubscription()
                .expectNext()
                .verifyComplete();
    }



}