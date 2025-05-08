package com.locadora.service;

import com.locadora.dto.FilmeDto;
import com.locadora.model.Exemplar;
import com.locadora.model.Filme;
import com.locadora.repository.ExemplarRepository;
import com.locadora.repository.FilmeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FilmeService {

    private final ExemplarRepository exemplarRepository;
    private final FilmeRepository filmeRepository;

    public FilmeService(FilmeRepository filmeRepository, ExemplarRepository exemplarRepository) {
        this.filmeRepository = filmeRepository;
        this.exemplarRepository = exemplarRepository;
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

    public Filme salvar(Filme filme) {
        return filmeRepository.save(filme);
    }

    public List<Filme> listarTodos() {
        return filmeRepository.findAll();
    }

    public boolean existeTitulo(String titulo) {
        return filmeRepository.existsByTituloIgnoreCase(titulo.trim());
    }

    public Optional<Filme> buscarPorId(Long id) {
        return filmeRepository.findById(id);
    }

    public void atualizarFilme(Filme filmeEditado) {
        Filme original = filmeRepository.findById(filmeEditado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));

        original.setAtivo(filmeEditado.isAtivo());
        filmeRepository.save(original);

        if (!filmeEditado.isAtivo()) {
            desativarFilmeComExemplares(original);
        }
    }


    @Transactional
    public void desativarFilmeComExemplares(Filme filme) {
        filme.setAtivo(false);
        List<Exemplar> exemplares = exemplarRepository.findByFilme(filme);
        for (Exemplar e : exemplares) {
            e.setAtivo(false);
        }
        exemplarRepository.saveAll(exemplares);
        filme.setExemplaresDisponiveis(0);
        filmeRepository.save(filme);
    }

    @Transactional
    public void salvarFilme(Filme filme, boolean adicionarExemplares, Integer quantidade, LocalDate dataCadastro) {
        if (existeTitulo(filme.getTitulo())) {
            throw new IllegalArgumentException("Já existe um filme com este título.");
        }

        if (adicionarExemplares) {
            if (quantidade == null || quantidade <= 0) {
                throw new IllegalArgumentException("A quantidade de exemplares deve ser maior que zero.");
            }

            filme.setAtivo(true);
            Filme filmeSalvo = filmeRepository.save(filme);

            for (int i = 0; i < quantidade; i++) {
                Exemplar ex = new Exemplar();
                ex.setFilme(filmeSalvo);
                ex.setAtivo(true);
                ex.setDataCadastro(dataCadastro != null ? dataCadastro : LocalDate.now());
                exemplarRepository.save(ex);
            }

            long ativos = exemplarRepository.countByFilmeAndAtivoTrue(filmeSalvo);
            filmeSalvo.setExemplaresDisponiveis(ativos);
            filmeRepository.save(filmeSalvo);
        } else {
            filme.setAtivo(false);
            filme.setExemplaresDisponiveis(0);
            filmeRepository.save(filme);
        }
    }

}
