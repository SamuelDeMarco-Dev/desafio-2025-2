package com.locadora.controller;

import com.locadora.model.Exemplar;
import com.locadora.model.Filme;
import com.locadora.service.ExemplarService;
import com.locadora.service.FilmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/exemplares")
public class ExemplarController {

    private final ExemplarService exemplarService;
    private final FilmeService filmeService;

    @Autowired
    public ExemplarController(ExemplarService exemplarService, FilmeService filmeService) {
        this.exemplarService = exemplarService;
        this.filmeService = filmeService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("exemplares", exemplarService.listarTodos());
        return "exemplares";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("exemplar", new Exemplar());
        model.addAttribute("filmes", filmeService.listarTodos());
        return "exemplar-form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Exemplar exemplar, Model model) {
        try {
            exemplarService.salvar(exemplar);
        } catch (IllegalStateException e) {
            model.addAttribute("erro", e.getMessage());
            return "exemplar-form";
        }
        return "redirect:/exemplares";
    }

    @PostMapping("/atualizar")
    public String atualizar(@ModelAttribute Exemplar exemplar) {
        exemplarService.atualizar(exemplar);
        return "redirect:/exemplares";
    }

    @GetMapping("/por-filme")
    public String listarPorFilme(@RequestParam Long id, Model model) {
        Filme filme = filmeService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
        List<Exemplar> exemplares = exemplarService.buscarPorFilme(filme);
        model.addAttribute("filme", filme);
        model.addAttribute("exemplares", exemplares);
        return "exemplares";
    }

}
