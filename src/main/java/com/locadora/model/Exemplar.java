package com.locadora.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class Exemplar {
    private int id;
    private Filme filme;
    private Date dataCadastro;
    private boolean ativo;

    public Exemplar(int id, Filme filme, Date dataCadastro, boolean ativo) {
        this.id = id;
        this.filme = filme;
        this.dataCadastro = dataCadastro;
        this.ativo = ativo;
    }
}
