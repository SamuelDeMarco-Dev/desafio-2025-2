CREATE TABLE locacao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exemplar_id BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    data_locacao DATE NOT NULL,
    data_devolucao DATE NOT NULL,
    data_devolvido DATE,
    qr_code LONGTEXT,
    finalizada BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_locacao_exemplar
        FOREIGN KEY (exemplar_id)
        REFERENCES exemplar(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);