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
    public String formularioNovo(@RequestParam(required = false) Long filmeId, Model model) {
        Exemplar exemplar = new Exemplar();

        if (filmeId != null) {
            Filme filme = filmeService.buscarPorId(filmeId)
                    .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
            exemplar.setFilme(filme);
            model.addAttribute("filmeBloqueado", true);
        }

        model.addAttribute("exemplar", exemplar);
        model.addAttribute("filmes", filmeService.listarTodos());
        return "exemplar-form";
    }

    @PostMapping("/salvar-multiplos")
    public String salvarMultiplos(@ModelAttribute Exemplar exemplar,
                                  @RequestParam int quantidade,
                                  @RequestParam("filme.id") Long filmeId,
                                  Model model) {
        try {
            Filme filme = filmeService.buscarPorId(filmeId)
                    .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
            exemplar.setFilme(filme);

            System.out.println("Salvando " + quantidade + " exemplares para filme ID: " + filmeId);
            System.out.println("Checkbox ativo: " + exemplar.getAtivo());

            exemplarService.salvarMultiplos(exemplar, quantidade);
            return "redirect:/filmes";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("exemplar", exemplar);
            model.addAttribute("filmes", filmeService.listarTodos());
            model.addAttribute("filmeBloqueado", exemplar.getFilme() != null);
            return "exemplar-form";
        }
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

    @PostMapping("/inativar/{id}")
    public String inativarExemplar(@PathVariable Long id, @RequestParam("filmeId") Long filmeId) {
        exemplarService.inativarExemplar(id);
        return "redirect:/exemplares/por-filme?id=" + filmeId;
    }

    @PostMapping("/reativar/{id}")
    public String reativarExemplar(@PathVariable Long id, @RequestParam("filmeId") Long filmeId, Model model) {
        try {
            exemplarService.reativarExemplar(id);
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return listarPorFilme(filmeId, model);
        }
        return "redirect:/exemplares/por-filme?id=" + filmeId;
    }

}
