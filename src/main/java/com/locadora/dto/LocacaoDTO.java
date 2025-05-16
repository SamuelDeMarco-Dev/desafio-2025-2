package com.locadora.dto;

import com.locadora.model.Locacao;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LocacaoDTO {
    private String nome;
    private String cpf;
    private String filme;
    private LocalDate dataLocacao;
    private LocalDate dataDevolucao;

    public LocacaoDTO(Locacao locacao) {
        this.nome = locacao.getNome();
        this.cpf = locacao.getCpf();
        this.filme = locacao.getExemplar().getFilme().getTitulo();
        this.dataLocacao = locacao.getDataLocacao();
        this.dataDevolucao = locacao.getDataDevolucao();
    }

}
