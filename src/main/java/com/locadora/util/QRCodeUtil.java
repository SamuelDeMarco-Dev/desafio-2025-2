package com.locadora.util;

import com.locadora.model.Locacao;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class QRCodeUtil {

    private static final String QR_CODE_API = "https://api.apgy.in/qr/";
    private final RestTemplate restTemplate;

    public QRCodeUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateQRCode(Locacao locacao) {
        try {
            String dadosJson = String.format("{\"cpf\":\"%s\",\"telefone\":\"%s\",\"dataLocacao\":\"%s\",\"dataDevolucao\":\"%s\"}",
                    locacao.getCpf(),
                    locacao.getTelefone(),
                    locacao.getDataLocacao(),
                    locacao.getDataDevolucao()
            );

            String encoded = URLEncoder.encode(dadosJson, StandardCharsets.UTF_8);
            String url = QR_CODE_API + "?data=" + encoded + "&size=300";

            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code", e);
        }
    }
}
