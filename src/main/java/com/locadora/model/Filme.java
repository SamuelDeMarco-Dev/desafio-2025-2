package com.locadora.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "filme")
@Getter
@Setter
@NoArgsConstructor
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean ativo;

    @Column(name = "exemplares_disponiveis")
    private long exemplaresDisponiveis;

    // Campos vindos da API TMDB
    private String titulo;

    @Column(length = 1000)
    private String resumo;

    private String pontuacao;

    private LocalDate lancamento;

    public Filme(boolean ativo, long exemplaresDisponiveis, String titulo, String resumo, String pontuacao, LocalDate lancamento) {
        this.ativo = ativo;
        this.exemplaresDisponiveis = exemplaresDisponiveis;
        this.titulo = titulo;
        this.resumo = resumo;
        this.pontuacao = pontuacao;
        this.lancamento = lancamento;
    }

    public String getDataLancamentoFormatada() {
        return this.lancamento != null ? this.lancamento.toString() : "";
    }

    public String getPontuacaoFormatada() {
        return this.pontuacao != null ? String.valueOf(this.pontuacao) : "";
    }
}
