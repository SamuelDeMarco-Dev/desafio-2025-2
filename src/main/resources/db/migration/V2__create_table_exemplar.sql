CREATE TABLE exemplar (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ativo BOOLEAN NOT NULL,
    data_cadastro DATE,
    filme_id BIGINT,
    CONSTRAINT fk_exemplar_filme
        FOREIGN KEY (filme_id)
        REFERENCES filme(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);
