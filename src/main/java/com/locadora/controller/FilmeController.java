package com.locadora.controller;

import com.locadora.dto.FilmeDto;
import com.locadora.model.Filme;
import com.locadora.service.FilmeService;
import com.locadora.util.TmdbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/filmes")
public class FilmeController {

    private final FilmeService filmeService;
    private final TmdbClient tmdbClient;

    @Autowired
    public FilmeController(FilmeService filmeService, TmdbClient tmdbClient) {
        this.filmeService = filmeService;
        this.tmdbClient = tmdbClient;
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
    public String buscarDaApiETemporariamenteSalvar(@RequestParam String titulo, Model model) {
        FilmeDto dto = tmdbClient.buscarFilmePorTitulo(titulo);

        if (dto == null) {
            model.addAttribute("erro", "Filme não encontrado na API.");
            return "filme-form";
        }

        Filme filme = filmeService.converterParaFilme(dto);
        filmeService.salvar(filme);

        model.addAttribute("filmes", filmeService.listarTodos());
        return "redirect:/filmes";
    }
}
