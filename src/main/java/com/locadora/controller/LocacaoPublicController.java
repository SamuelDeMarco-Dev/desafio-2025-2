package com.locadora.controller;

import com.locadora.dto.LocacaoDTO;
import com.locadora.model.Locacao;
import com.locadora.service.LocacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class LocacaoPublicController {

    private final LocacaoService locacaoService;

    public LocacaoPublicController(LocacaoService locacaoService) {
        this.locacaoService = locacaoService;
    }

    //http://localhost:8081/api/public/locacoes?cpf=999.999.999-99
    @GetMapping("/locacoes")
    public ResponseEntity<?> consultarPorCpf(@RequestParam String cpf) {
        List<Locacao> locacoes = locacaoService.consultarLocacoesPendentesPorCpf(cpf);
        List<LocacaoDTO> dtoList = locacoes.stream().map(LocacaoDTO::new).toList();
        return ResponseEntity.ok(dtoList);
    }
}

