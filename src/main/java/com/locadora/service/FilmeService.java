package com.locadora.service;

import com.locadora.dto.FilmeDto;
import com.locadora.model.Filme;
import com.locadora.repository.FilmeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;

    public FilmeService(FilmeRepository filmeRepository) {
        this.filmeRepository = filmeRepository;
    }

    public Filme converterParaFilme(FilmeDto dto) {
        Filme filme = new Filme();
        filme.setTitulo(dto.getTitulo());
        filme.setResumo(dto.getResumo());
        filme.setPontuacao(dto.getPontuacao());

        if (dto.getDataLancamento() != null && !dto.getDataLancamento().isEmpty()) {
            filme.setLancamento(LocalDate.parse(dto.getDataLancamento()));
        }

        filme.setAtivo(true);
        filme.setExemplaresDisponiveis(0);

        return filme;
    }

    public void salvar(Filme filme) {
        filmeRepository.save(filme);
    }

    public List<Filme> listarTodos() {
        return filmeRepository.findAll();
    }
}
