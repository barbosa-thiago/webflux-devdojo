package com.webflux.thiago.webfluxdevdojo.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("anime")
public class Anime {
    @Id
    private Integer id;

    @NotEmpty(message = "the field name cannot be empty")
    @NotNull
    private String name;
}
