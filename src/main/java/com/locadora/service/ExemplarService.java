package com.locadora.service;

import com.locadora.model.Exemplar;
import com.locadora.model.Filme;
import com.locadora.repository.ExemplarRepository;
import com.locadora.repository.FilmeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExemplarService {

    private final ExemplarRepository exemplarRepository;
    private final FilmeRepository filmeRepository;

    public ExemplarService(ExemplarRepository exemplarRepository, FilmeRepository filmeRepository) {
        this.exemplarRepository = exemplarRepository;
        this.filmeRepository = filmeRepository;
    }

    public List<Exemplar> listarTodos() {
        return exemplarRepository.findAll();
    }

    @Transactional
    public void salvar(Exemplar exemplar) {
        Filme filme = exemplar.getFilme();

        if (!filme.isAtivo()) {
            throw new IllegalStateException("Não é possível adicionar exemplar a um filme inativo.");
        }

        exemplarRepository.save(exemplar);
        atualizarExemplaresDisponiveis(filme);
    }

    @Transactional
    public void atualizar(Exemplar exemplarAtualizado) {
        Optional<Exemplar> antigo = exemplarRepository.findById(exemplarAtualizado.getId());

        if (antigo.isPresent()) {
            exemplarRepository.save(exemplarAtualizado);

            if (antigo.get().isAtivo() != exemplarAtualizado.isAtivo()) {
                atualizarExemplaresDisponiveis(exemplarAtualizado.getFilme());
            }
        }
    }

    private void atualizarExemplaresDisponiveis(Filme filme) {
        long ativos = exemplarRepository.countByFilmeAndAtivoTrue(filme);
        filme.setExemplaresDisponiveis(ativos);
        filmeRepository.save(filme);
    }

    public Optional<Exemplar> buscarPorId(Long id) {
        return exemplarRepository.findById(id);
    }

    public List<Exemplar> buscarPorFilme(Filme filme) {
        return exemplarRepository.findByFilme(filme);
    }

}
