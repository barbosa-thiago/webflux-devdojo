package com.webflux.thiago.webfluxdevdojo.service;

import com.webflux.thiago.webfluxdevdojo.domain.Anime;
import com.webflux.thiago.webfluxdevdojo.repository.AnimeRepository;
import com.webflux.thiago.webfluxdevdojo.util.AnimeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepositoryMock;

    private final Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    private static void blockHoundSetUp(){
        BlockHound.install(builder -> builder
                .allowBlockingCallsInside("java.util.UUID", "randomUUID"));
    }
    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @BeforeEach
    public void setUp(){
        BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(Flux.just(anime));
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeRepositoryMock
                .saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
                .thenReturn(Flux.just(anime, anime));

        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());

        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createValidAnime()))
                .thenReturn(Mono.empty());

    }

    @Test
    @DisplayName("FindAll return a flux of anime")
    public void findAll_ReturnAnimeFlux_WhenSuccesful(){

        StepVerifier.create(animeService.findAll())
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("FindById return a mono of anime when it exists")
    public void findById_ReturnAnimeMono_WhenSuccesful(){
        StepVerifier.create(animeService.findById(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("FindById return mono error when anime does not exists")
    public void findById_ReturnMonoError_WhenEmptyMonoIsReturned(){

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(animeService.findById(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("Save creates anime when succesful")
    public void save_CreatesAnime_WhenSuccesful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        StepVerifier.create(animeService.save(animeToBeSaved))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("SaveAll creates list of anime when succesful")
    public void saveAll_CreatesAnimeList_WhenSuccesful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        StepVerifier.create(animeService.saveAll(List.of(animeToBeSaved, animeToBeSaved)))
                .expectSubscription()
                .expectNext(anime, anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("SaveAll return mono error when name is invalid")
    public void saveAll_ReturnMonoError_WhenNameIsInvalid(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        BDDMockito.when(animeRepositoryMock.saveAll(ArgumentMatchers.anyIterable()))
                .thenReturn(Flux.just(anime, anime.withName("")));

        StepVerifier.create(animeService.saveAll(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
                .expectSubscription()
                .expectNext(anime)
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("Delete removes anime when succesful")
    public void delete_RemovesAnime_WhenSuccesful(){
        StepVerifier.create(animeService.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("Delete return mono error when anime doesnt exist")
    public void delete_ReturnMonoError_WhenAnimeDoesntExist(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        StepVerifier.create(animeService.delete(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("update save updated anime and return empty mono when succesful")
    public void update_SavesUpdatedAnime_WhenSuccesful(){

        StepVerifier.create(animeService.update(AnimeCreator.createValidAnime()))
                .expectSubscription()
                .expectNext()
                .verifyComplete();
    }

    @Test
    @DisplayName("Update return mono error when anime doesnt exist")
    public void update_ReturnMonoError_WhenAnimeDoesntExist(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        StepVerifier.create(animeService.update(AnimeCreator.createValidAnime()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

}