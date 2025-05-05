package com.locadora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilmeDto {

    @JsonProperty("title")
    private String titulo;

    @JsonProperty("overview")
    private String resumo;

    @JsonProperty("vote_average")
    private String pontuacao;

    @JsonProperty("release_date")
    private String dataLancamento;
}
