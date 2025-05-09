package com.locadora.service;

import com.locadora.model.Exemplar;
import com.locadora.model.Locacao;
import com.locadora.repository.ExemplarRepository;
import com.locadora.repository.LocacaoRepository;
import com.locadora.util.QRCodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LocacaoService {

    private final LocacaoRepository locacaoRepository;
    private final ExemplarRepository exemplarRepository;
    private final QRCodeUtil qrCodeUtil;

    public LocacaoService(LocacaoRepository locacaoRepository,
                          ExemplarRepository exemplarRepository,
                          QRCodeUtil qrCodeUtil) {
        this.locacaoRepository = locacaoRepository;
        this.exemplarRepository = exemplarRepository;
        this.qrCodeUtil = qrCodeUtil;
    }

    @Transactional
    public Locacao realizarLocacao(Locacao locacao) {
        try {
            Exemplar exemplar = exemplarRepository.findById(locacao.getExemplar().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado"));
            locacao.setExemplar(exemplar);

            if (!exemplar.isAtivo()) {
                throw new IllegalStateException("Exemplar não está ativo para locação");
            }

            boolean exemplarEmUso = locacaoRepository.findByExemplarId(exemplar.getId())
                    .stream()
                    .anyMatch(l -> !l.isFinalizada());
            if (exemplarEmUso) {
                throw new IllegalStateException("Exemplar já está locado");
            }

            exemplar.setAtivo(false);
            exemplarRepository.save(exemplar);

            locacao.setDataLocacao(LocalDate.now());
            locacao.setDataDevolucao(LocalDate.now().plusDays(7));
            locacao.setFinalizada(false);

            String qrCode = qrCodeUtil.generateQRCode((locacao));
            System.out.println("[QRCode BASE64] -> " + qrCode);

            locacao.setQrCode(qrCode);

            System.out.println("[SALVANDO LOCACACAO] " + locacao.getNome() + " - " + locacao.getCpf());

            return locacaoRepository.save(locacao);

        } catch (Exception e) {
            System.err.println("[ERRO LOCAÇÃO] " + e.getMessage());
            throw e;
        }
    }



    @Transactional
    public Locacao finalizarLocacao(Long id) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Locação não encontrada"));

        Exemplar exemplar = locacao.getExemplar();
        exemplar.setAtivo(true);
        exemplarRepository.save(exemplar);

        locacao.setDataDevolvido(LocalDate.now());
        locacao.setFinalizada(true);

        return locacaoRepository.save(locacao);
    }

    public List<Locacao> listarLocacoesAtivas() {
        return locacaoRepository.findByFinalizadaFalse();
    }

    public List<Locacao> listarTodasLocacoes() {
        return locacaoRepository.findAll();
    }
}