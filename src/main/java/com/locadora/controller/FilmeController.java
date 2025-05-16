package com.locadora.controller;

import com.locadora.dto.FilmeDto;
import com.locadora.model.Filme;
import com.locadora.service.ExemplarService;
import com.locadora.service.FilmeService;
import com.locadora.util.TmdbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/filmes")
public class FilmeController {

    private final FilmeService filmeService;
    private final TmdbClient tmdbClient;
    private final ExemplarService exemplarService;

    @Autowired
    public FilmeController(FilmeService filmeService, TmdbClient tmdbClient, ExemplarService exemplarService) {
        this.filmeService = filmeService;
        this.tmdbClient = tmdbClient;
        this.exemplarService = exemplarService;
    }

    @GetMapping
    public String listarFilmes(Model model) {
        model.addAttribute("filmes", filmeService.listarTodos());
        return "filmes";
    }

    @GetMapping("/novo")
    public String formularioNovoFilme() {
        return "filme-form";
    }

    @PostMapping("/buscar-api")
    public String buscarDaApi(@RequestParam String titulo, Model model) {
        if (filmeService.existeTitulo(titulo)) {
            model.addAttribute("erro", "Esse filme já foi cadastrado.");
            return "filme-form";
        }

        FilmeDto dto = tmdbClient.buscarFilmePorTitulo(titulo);
        if (dto == null) {
            model.addAttribute("erro", "Filme não encontrado na API.");
            return "filme-form";
        }
        Filme filme = filmeService.converterParaFilme(dto);
        filmeService.salvarFilme(filme, false, null, null);
        return "redirect:/filmes";
    }

    @GetMapping("/editar")
    public String editarFilme(@RequestParam Long id, Model model) {
        Filme filme = filmeService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
        model.addAttribute("filme", filme);
        return "filme-editar";
    }

    @PostMapping("/salvar")
    public String salvarConfirmado(
            @RequestParam String titulo,
            @RequestParam String resumo,
            @RequestParam String pontuacao,
            @RequestParam String lancamento,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam long exemplaresDisponiveis
    ) {
        Filme filme = new Filme();
        filme.setTitulo(titulo);
        filme.setResumo(resumo);
        filme.setPontuacao(pontuacao);
        filme.setLancamento(LocalDate.parse(lancamento));
        filme.setAtivo(Boolean.TRUE.equals(ativo));
        filme.setExemplaresDisponiveis(exemplaresDisponiveis);
        filmeService.salvar(filme);
        return "redirect:/filmes";
    }

    @GetMapping("/editar/{id}")
    public String editarFilmeExemplar(@PathVariable Long id, Model model) {
        Filme filme = filmeService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
        model.addAttribute("filme", filme);
        model.addAttribute("exemplares", exemplarService.listarPorFilme(filme.getId()));
        return "filme-editar";
    }

    @PostMapping("/editar")
    public String atualizarFilme(@ModelAttribute Filme filme, Model model) {
        try {
            filmeService.atualizarFilme(filme);
            return "redirect:/filmes";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("filme", filme);
            model.addAttribute("exemplares", exemplarService.listarPorFilme(filme.getId()));
            return "filme-editar";
        }
    }



}
