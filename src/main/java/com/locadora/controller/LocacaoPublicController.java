package com.locadora.controller;

import com.locadora.model.Locacao;
import com.locadora.service.LocacaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class LocacaoPublicController {

    private final LocacaoService locacaoService;

    public LocacaoPublicController(LocacaoService locacaoService) {
        this.locacaoService = locacaoService;
    }

    @GetMapping("/locacoes")
    public List<Locacao> consultarPorCpf(@RequestParam String cpf) {
        return locacaoService.consultarLocacoesPendentesPorCpf(cpf);
    }

    //teste URL publica
    @GetMapping("/ping")
    public String ping() {
        return "ok";
    }
}
