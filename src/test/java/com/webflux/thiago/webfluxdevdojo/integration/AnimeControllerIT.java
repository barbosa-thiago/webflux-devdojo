package com.webflux.thiago.webfluxdevdojo.integration;

import com.webflux.thiago.webfluxdevdojo.domain.Anime;
import com.webflux.thiago.webfluxdevdojo.repository.AnimeRepository;
import com.webflux.thiago.webfluxdevdojo.util.AnimeCreator;
import com.webflux.thiago.webfluxdevdojo.util.WebTestClientUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class AnimeControllerIT {

    private static final String REGULAR_USER = "user";
    private static final String ADMIN_USER = "thiago";

    @Autowired
    WebTestClient client;

    @Autowired
    WebTestClientUtil webTestClientUtil;
    @MockBean
    AnimeRepository animeRepositoryMock;


    Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    public static void blockHoundSetUp() {
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
    public void setUp() {

        BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(Flux.just(anime));
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));
        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
                .thenReturn(Mono.just(anime));
        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());
        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createValidAnime()))
                .thenReturn(Mono.empty());

        BDDMockito.when(animeRepositoryMock
                .saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
                .thenReturn(Flux.just(anime, anime));
    }

    @Test
    @DisplayName("FindAll list all animes when user is succesfully authenticated with role ADMIN")
    @WithUserDetails(ADMIN_USER)
    void findAll_ListAnimes_WhenSuccesful(){
        client
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(anime.getId())
                .jsonPath("$.[0].name").isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("FindAll return forbidden when user is succesfully authenticated without role ADMIN")
    @WithUserDetails(REGULAR_USER)
    void findAll_ReturnForbidden_WhenAuthorizedUserHasNotRoleAdmin(){
        client
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("FindAll return unauthorized when user is not authenticated")
    void findAll_ReturnUnauthorized_WhenUserIsNotAuthenticated(){
        client
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().isUnauthorized();
    }
    @Test
    @DisplayName("FindAll list all animes when user is succesfully authenticated with role ADMIN")
    @WithUserDetails(ADMIN_USER)
    void findAll_ListAnimes_WhenSuccesful_Flavor2(){
        client
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Anime.class)
                .hasSize(1)
                .contains(anime);
    }

    @Test
    @DisplayName("FindById return a mono of anime when user is succesfully authenticated with role USER")
    @WithUserDetails(REGULAR_USER)
    void findById_ReturnAnimeMono_WhenSuccesful(){
        client
                .get()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Anime.class)
                .isEqualTo(anime);
    }

    @Test
    @DisplayName("FindById return mono error when anime does not exists")
    @WithUserDetails(REGULAR_USER)
    void findById_ReturnMonoError_WhenEmptyMonoIsReturned(){

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        client
                .get()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException happened");
    }

    @Test
    @DisplayName("Save creates anime when user is succesfully authenticated with role ADMIN")
    @WithUserDetails(ADMIN_USER)
    void save_CreatesAnime_WhenSuccesful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        client.post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(animeToBeSaved))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Anime.class)
                .isEqualTo(anime);
    }

    @Test
    @DisplayName("SaveBatch creates anime when user is succesfully authenticated with role ADMIN")
    @WithUserDetails(ADMIN_USER)
    void saveBatch_CreatesAnime_WhenSuccesful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        client.post()
                .uri("/animes/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(Anime.class)
                .hasSize(2)
                .contains(anime);
    }

    @Test
    @DisplayName("Save returns mono error with bad request when name is empty")
    @WithUserDetails(ADMIN_USER)
    void save_ReturnMonoError_WhenNameIsEmpty(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");
        client.post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(animeToBeSaved))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("SaveBatch returns mono error with bad request when name is empty")
    @WithUserDetails(ADMIN_USER)
    void saveBatch_ReturnMonoError_WhenNameIsEmpty(){
        BDDMockito.when(animeRepositoryMock
                .saveAll(ArgumentMatchers.anyIterable()))
                .thenReturn(Flux.just(anime, anime.withName("")));
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        client.post()
                .uri("/animes/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("Delete removes anime when user is succesfully authenticated with role ADMIN")
    @WithUserDetails(ADMIN_USER)
    void delete_RemovesAnime_WhenSuccesful(){
        client.delete()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Delete return mono error when anime doesnt exist")
    @WithUserDetails(REGULAR_USER)
    void delete_ReturnMonoError_WhenAnimeDoesntExist(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        client.delete()
                .uri("anime/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException happened");
    }

    @Test
    @DisplayName("update save updated anime and return empty mono when user is succesfully authenticated with role ADMIN")
    @WithUserDetails(ADMIN_USER)
    void update_SavesUpdatedAnime_WhenSuccesful(){

        client.put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(anime))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Update return mono error when anime doesnt exist")
    @WithUserDetails(ADMIN_USER)
    void update_ReturnMonoError_WhenAnimeDoesntExist(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        client.put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(anime))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException happened");
    }
}
