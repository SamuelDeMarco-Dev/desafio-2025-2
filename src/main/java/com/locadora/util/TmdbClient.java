package com.locadora.util;

import com.locadora.dto.FilmeDto;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class TmdbClient {

    private static final String API_KEY = "a2e58e84019e303a2b15d6b4845f5df9";
    private static final String TMDB_URL = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query={query}&language=pt-BR";

    private final RestTemplate restTemplate = new RestTemplate();

    public FilmeDto buscarFilmePorTitulo(String titulo) {
        try {
            TmdbResponse response = restTemplate.getForObject(TMDB_URL, TmdbResponse.class, titulo);
            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                return response.getResults().get(0); // pega o primeiro resultado
            }
        } catch (Exception e) {
            log.error("Erro ao consultar filme no TMDB", e);
        }
        return null;
    }

    // Classe interna para mapear a resposta completa da API
    @Setter
    private static class TmdbResponse {
        private List<FilmeDto> results;

        public List<FilmeDto> getResults() {
            return results != null ? results : Collections.emptyList();
        }

    }
}
