package com.webflux.thiago.webfluxdevdojo.util;

import com.webflux.thiago.webfluxdevdojo.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeToBeSaved() {
        return Anime.builder()
                .name("Yugi Oh")
                .build();
    }

    public static Anime createValidAnime() {
        return Anime.builder()
                .id(1)
                .name("Yugi Oh")
                .build();
    }

    public static Anime createAnimeUpdated() {
        return Anime.builder()
                .id(1)
                .name("Yugi Oh part 2")
                .build();
    }
}
