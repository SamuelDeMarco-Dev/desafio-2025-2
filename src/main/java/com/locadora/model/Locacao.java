package com.locadora.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
@NoArgsConstructor
public class Locacao {
    private int id;
    private Exemplar exemplares;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private Date dataLocacao;
    private Date dataDevolucao;
    private Date dataDevolvido;
    //Vindo da API QRCODE
    private String qrCode;

    public Locacao(int id, Exemplar exemplares, String nome, String cpf, String email, String telefone, Date dataLocacao, Date dataDevolucao, Date dataDevolvido, String qrCode) {
        this.id = id;
        this.exemplares = exemplares;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataLocacao = dataLocacao;
        this.dataDevolucao = dataDevolucao;
        this.dataDevolvido = dataDevolvido;
        this.qrCode = qrCode;
    }
}
