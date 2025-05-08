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

    @GetMapping("/editar")
    public String editarFilme(@RequestParam Long id, Model model) {
        Filme filme = filmeService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
        model.addAttribute("filme", filme);
        return "filme-editar";
    }

    @PostMapping("/editar")
    public String atualizarFilme(@ModelAttribute Filme filme) {
        filmeService.atualizarFilme(filme);
        return "redirect:/filmes";
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
        model.addAttribute("filme", filme);
        return "filme-form-etapa2";
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

    @PostMapping("/confirmar")
    public String confirmarFilme(@ModelAttribute Filme filme, @RequestParam boolean addExemplares, Model model) {
        if (!addExemplares) {
            filmeService.salvarFilme(filme, false, null, null);
            return "redirect:/filmes";
        }
        model.addAttribute("filme", filme);
        return "exemplar-form";
    }

    @PostMapping("/salvar-com-exemplares")
    public String salvarComExemplares(@ModelAttribute Filme filme,
                                      @RequestParam int quantidade,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCadastro,
                                      Model model) {
        try {
            filmeService.salvarFilme(filme, true, quantidade, dataCadastro);
            return "redirect:/filmes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("filme", filme);
            return "exemplar-form";
        }
    }

    @PostMapping("/adicionar-exemplares")
    public String adicionarExemplares(@RequestParam Long id,
                                      @RequestParam int quantidade,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCadastro,
                                      Model model) {
        try {
            Filme filme = filmeService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
            filmeService.salvarFilme(filme, true, quantidade, dataCadastro);
            return "redirect:/filmes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("filme", filmeService.buscarPorId(id).orElse(null));
            return "filme-editar";
        }
    }


}
