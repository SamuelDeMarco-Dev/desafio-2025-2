package com.locadora.repository;

import com.locadora.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
    List<Locacao> findByFinalizadaFalse();
    List<Locacao> findByExemplarId(Long exemplarId);
    List<Locacao> findByCpfAndFinalizadaFalse(String cpf);
}