package com.locadora.controller;

import com.locadora.model.Locacao;
import com.locadora.service.ExemplarService;
import com.locadora.service.LocacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/locacoes")
public class LocacaoController {

    private final LocacaoService locacaoService;
    private final ExemplarService exemplarService;

    public LocacaoController(LocacaoService locacaoService,
                             ExemplarService exemplarService) {
        this.locacaoService = locacaoService;
        this.exemplarService = exemplarService;
    }

    @GetMapping
    public String listarLocacoes(@RequestParam(required = false) String filtro, Model model) {
        List<Locacao> locacoes;

        if (filtro != null && !filtro.isBlank()) {
            locacoes = locacaoService.buscarPorFiltro(filtro);
        } else {
            locacoes = locacaoService.listarTodasLocacoes();
        }

        model.addAttribute("locacoes", locacoes);
        model.addAttribute("filtro", filtro);
        return "locacoes";
    }

    @GetMapping("/ativas")
    public String listarLocacoesAtivas(Model model) {
        List<Locacao> locacoes = locacaoService.listarLocacoesAtivas();
        model.addAttribute("locacoes", locacoes);
        return "locacoes";
    }

    @GetMapping("/nova")
    public String formularioNovaLocacao(Model model) {
        model.addAttribute("locacao", new Locacao());
        model.addAttribute("exemplares", exemplarService.listarTodosAtivos());
        return "locacao-form";
    }

    @PostMapping("/salvar")
    public String salvarLocacao(@ModelAttribute Locacao locacao, Model model) {
        try {
            locacaoService.realizarLocacao(locacao);
            return "redirect:/locacoes";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("exemplares", exemplarService.listarTodosAtivos());
            return "locacao-form";
        }
    }

    @GetMapping("/finalizar/{id}")
    public String finalizarLocacao(@PathVariable Long id) {
        locacaoService.finalizarLocacao(id);
        return "redirect:/locacoes";
    }

}