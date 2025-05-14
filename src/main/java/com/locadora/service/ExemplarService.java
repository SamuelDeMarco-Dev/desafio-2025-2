package com.locadora.service;

import com.locadora.model.Exemplar;
import com.locadora.model.Filme;
import com.locadora.model.Locacao;
import com.locadora.repository.ExemplarRepository;
import com.locadora.repository.FilmeRepository;
import com.locadora.repository.LocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExemplarService {

    private final ExemplarRepository exemplarRepository;
    private final FilmeRepository filmeRepository;
    private final LocacaoRepository locacaoRepository;

    public ExemplarService(ExemplarRepository exemplarRepository,
                           FilmeRepository filmeRepository,
                           LocacaoRepository locacaoRepository) {
        this.exemplarRepository = exemplarRepository;
        this.filmeRepository = filmeRepository;
        this.locacaoRepository = locacaoRepository;
    }

    public List<Exemplar> listarTodos() {
        return exemplarRepository.findAll();
    }

    public List<Exemplar> listarTodosAtivos() {
        return exemplarRepository.findByAtivoTrue();
    }

    @Transactional
    public void salvar(Exemplar exemplar) {
        Filme filme = exemplar.getFilme();

        if (!filme.isAtivo()) {
            filme.setAtivo(true);
        }

        exemplarRepository.save(exemplar);
        atualizarExemplaresDisponiveis(filme);
    }

    @Transactional
    public void atualizar(Exemplar exemplarAtualizado) {
        Exemplar existente = exemplarRepository.findById(exemplarAtualizado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado"));

        boolean exemplarEstaLocado = locacaoRepository.findByExemplarId(existente.getId())
                .stream()
                .anyMatch(locacao -> !locacao.isFinalizada());

        if (exemplarEstaLocado) {
            throw new IllegalStateException("Exemplar está locado e não pode ser editado.");
        }

        exemplarRepository.save(exemplarAtualizado);

        if (existente.getAtivo() != exemplarAtualizado.getAtivo()) {
            atualizarExemplaresDisponiveis(exemplarAtualizado.getFilme());
        }
    }

    public Optional<Exemplar> buscarPorId(Long id) {
        return exemplarRepository.findById(id);
    }

    public List<Exemplar> buscarPorFilme(Filme filme) {
        return exemplarRepository.findByFilme(filme);
    }

    @Transactional
    public void inativarExemplar(Long id) {
        Exemplar exemplar = exemplarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado"));

        if (!locacaoRepository.findByExemplarId(exemplar.getId()).stream()
                .allMatch(Locacao::isFinalizada)) {
            throw new IllegalStateException("Não é possível inativar: há locações pendentes");
        }

        exemplar.setAtivo(false);
        exemplarRepository.save(exemplar);

        atualizarExemplaresDisponiveis(exemplar.getFilme());
    }

    private void atualizarExemplaresDisponiveis(Filme filme) {
        long ativos = exemplarRepository.countByFilmeAndAtivoTrue(filme);
        filme.setExemplaresDisponiveis(ativos);
        filmeRepository.save(filme);
    }

    @Transactional
    public void salvarMultiplos(Exemplar exemplarBase, int quantidade) {
        Filme filme = filmeRepository.findById(exemplarBase.getFilme().getId())
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));

        if (!filme.isAtivo()) {
            throw new IllegalStateException("Não é possível adicionar exemplares a um filme inativo.");
        }

        for (int i = 0; i < quantidade; i++) {
            Exemplar novo = new Exemplar();
            novo.setFilme(filme);
            novo.setDataCadastro(
                    exemplarBase.getDataCadastro() != null ? exemplarBase.getDataCadastro() : LocalDate.now()
            );
            novo.setAtivo(Boolean.TRUE.equals(exemplarBase.getAtivo()));
            exemplarRepository.saveAndFlush(novo);
        }
        atualizarExemplaresDisponiveis(filme);
    }

    @Transactional
    public void reativarExemplar(Long id) {
        Exemplar exemplar = exemplarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado"));

        boolean exemplarEstaLocado = locacaoRepository.findByExemplarId(exemplar.getId())
                .stream()
                .anyMatch(locacao -> !locacao.isFinalizada());

        if (exemplarEstaLocado) {
            throw new IllegalStateException("Não é possível reativar: exemplar ainda está locado.");
        }

        exemplar.setAtivo(true);
        exemplarRepository.save(exemplar);
        atualizarExemplaresDisponiveis(exemplar.getFilme());
    }

    public List<Exemplar> listarPorFilme(Long filmeId) {
        return exemplarRepository.findByFilmeId(filmeId);
    }

}
