package com.locadora.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "exemplar")
@Getter
@Setter
@NoArgsConstructor
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Um exemplar pertence a um único filme
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filme_id", nullable = false)
    private Filme filme;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    private boolean ativo;

    // Seta a data de cadastro automaticamente, se não for informada
    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDate.now();
        }
    }
}
