package com.locadora.service;

import com.locadora.dto.FilmeDto;
import com.locadora.model.Exemplar;
import com.locadora.model.Filme;
import com.locadora.repository.ExemplarRepository;
import com.locadora.repository.FilmeRepository;
import com.locadora.repository.LocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FilmeService {

    private final ExemplarRepository exemplarRepository;
    private final FilmeRepository filmeRepository;
    private final LocacaoRepository locacaoRepository;

    public FilmeService(FilmeRepository filmeRepository,
                        ExemplarRepository exemplarRepository,
                        LocacaoRepository locacaoRepository) {
        this.filmeRepository = filmeRepository;
        this.exemplarRepository = exemplarRepository;
        this.locacaoRepository = locacaoRepository;
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

        boolean estavaAtivo = original.isAtivo();
        boolean desejaInativar = !filmeEditado.isAtivo();

        if (estavaAtivo && desejaInativar) {
            desativarFilmeComExemplares(original);
        } else {
            original.setAtivo(filmeEditado.isAtivo());
            filmeRepository.save(original);
        }
    }

    @Transactional
    public void desativarFilmeComExemplares(Filme filme) {
        List<Exemplar> exemplares = exemplarRepository.findByFilme(filme);

        boolean algumLocado = exemplares.stream()
                .anyMatch(e -> locacaoRepository.findByExemplarId(e.getId())
                        .stream()
                        .anyMatch(l -> !l.isFinalizada()));

        if (algumLocado) {
            throw new IllegalStateException("Não é possível inativar: há exemplares locados.");
        }

        exemplares.stream()
                .forEach(e -> e.setAtivo(false));

        exemplarRepository.saveAll(exemplares);

        filme.setAtivo(false);
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

            List<Exemplar> exemplares = IntStream.range(0, quantidade)
                    .mapToObj(i -> {
                        Exemplar ex = new Exemplar();
                        ex.setFilme(filmeSalvo);
                        ex.setAtivo(true);
                        ex.setDataCadastro(dataCadastro != null ? dataCadastro : LocalDate.now());
                        return ex;
                    })
                    .collect(Collectors.toList());
            exemplarRepository.saveAll(exemplares);

            long ativos = exemplarRepository.countByFilmeAndAtivoTrue(filmeSalvo);
            filmeSalvo.setExemplaresDisponiveis(ativos);
            filmeRepository.save(filmeSalvo);
        } else {
            filme.setAtivo(false);
            filme.setExemplaresDisponiveis(0);
            filmeRepository.save(filme);
        }
    }

    public List<Filme> buscarPorFiltro(String filtro) {
        return filmeRepository.findAll().stream()
                .filter(filme ->
                        (filme.getId() != null && String.valueOf(filme.getId()).contains(filtro)) ||
                                (filme.getTitulo() != null && filme.getTitulo().toLowerCase().contains(filtro.toLowerCase())) ||
                                (filme.getLancamento() != null && filme.getLancamento().toString().contains(filtro))
                )
                .collect(Collectors.toList());
    }

}
