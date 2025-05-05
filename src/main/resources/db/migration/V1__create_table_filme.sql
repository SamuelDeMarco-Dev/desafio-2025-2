CREATE TABLE filme (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ativo BOOLEAN NOT NULL,
    exemplares_disponiveis BIGINT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    resumo VARCHAR(1000),
    pontuacao VARCHAR(10),
    lancamento DATE
);
