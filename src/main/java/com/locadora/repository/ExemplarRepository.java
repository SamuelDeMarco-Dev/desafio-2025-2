package com.locadora.repository;

import com.locadora.model.Exemplar;
import com.locadora.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {

    long countByFilmeAndAtivoTrue(Filme filme);

    List<Exemplar> findByFilme(Filme filme);
}
