package com.locadora.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "locacao")
@Getter
@Setter
@NoArgsConstructor
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exemplar_id", nullable = false)
    private Exemplar exemplar;

    private String nome;
    private String cpf;
    private String email;
    private String telefone;

    @Column(name = "data_locacao")
    private LocalDate dataLocacao;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    @Column(name = "data_devolvido")
    private LocalDate dataDevolvido;

    @Column(name = "qr_code")
    private String qrCode;

    private boolean finalizada;
}