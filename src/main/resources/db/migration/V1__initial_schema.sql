-- =====================================================
-- Migration V1: Initial Schema
-- Description: Creates all tables for the game store system
-- Author: Professional Refactoring - FASE 4
-- =====================================================

-- Table: tb_usuarios
CREATE TABLE tb_usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    foto VARCHAR(5000),
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario (usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: usuario_roles (ElementCollection)
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, role),
    CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (usuario_id) 
        REFERENCES tb_usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: tb_categoria
CREATE TABLE tb_categoria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tipo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    icone VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: tb_produtos
CREATE TABLE tb_produtos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    desconto DECIMAL(5,2) DEFAULT 0.00,
    estoque INT NOT NULL,
    plataforma VARCHAR(255) NOT NULL,
    desenvolvedor VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    data_lancamento DATE NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    categoria_id BIGINT,
    usuario_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id) 
        REFERENCES tb_categoria(id),
    CONSTRAINT fk_produto_usuario FOREIGN KEY (usuario_id) 
        REFERENCES tb_usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: produto_imagens (ElementCollection)
CREATE TABLE produto_imagens (
    produto_id BIGINT NOT NULL,
    imagens VARCHAR(500),
    CONSTRAINT fk_produto_imagens_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: tb_avaliacoes
CREATE TABLE tb_avaliacoes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nota INT NOT NULL,
    comentario VARCHAR(500),
    data_avaliacao DATETIME(6) NOT NULL,
    produto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_avaliacao_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_usuario FOREIGN KEY (usuario_id) 
        REFERENCES tb_usuarios(id) ON DELETE CASCADE,
    CONSTRAINT chk_nota CHECK (nota >= 1 AND nota <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: tb_favoritos
CREATE TABLE tb_favoritos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    data_adicionado DATETIME(6) NOT NULL,
    usuario_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_favorito_usuario_produto (usuario_id, produto_id),
    CONSTRAINT fk_favorito_usuario FOREIGN KEY (usuario_id) 
        REFERENCES tb_usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorito_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: tb_carrinho_itens
CREATE TABLE tb_carrinho_itens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    desconto_unitario DECIMAL(10,2),
    data_adicionado DATETIME(6) NOT NULL,
    data_atualizacao DATETIME(6),
    usuario_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_carrinho_usuario_produto (usuario_id, produto_id),
    CONSTRAINT fk_carrinho_usuario FOREIGN KEY (usuario_id) 
        REFERENCES tb_usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_carrinho_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE,
    CONSTRAINT chk_quantidade CHECK (quantidade >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Indexes for Performance Optimization
-- =====================================================

-- Index on tb_produtos for common queries
CREATE INDEX idx_produto_categoria ON tb_produtos(categoria_id);
CREATE INDEX idx_produto_usuario ON tb_produtos(usuario_id);
CREATE INDEX idx_produto_ativo ON tb_produtos(ativo);
CREATE INDEX idx_produto_data_lancamento ON tb_produtos(data_lancamento);

-- Index on tb_avaliacoes for aggregation queries
CREATE INDEX idx_avaliacao_produto ON tb_avaliacoes(produto_id);
CREATE INDEX idx_avaliacao_usuario ON tb_avaliacoes(usuario_id);

-- Index on tb_favoritos for user queries
CREATE INDEX idx_favorito_usuario ON tb_favoritos(usuario_id);
CREATE INDEX idx_favorito_produto ON tb_favoritos(produto_id);

-- Index on tb_carrinho_itens for user queries
CREATE INDEX idx_carrinho_usuario ON tb_carrinho_itens(usuario_id);
CREATE INDEX idx_carrinho_produto ON tb_carrinho_itens(produto_id);

-- Index on tb_categoria for active categories
CREATE INDEX idx_categoria_ativo ON tb_categoria(ativo);
