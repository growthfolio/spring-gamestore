-- =====================================================
-- Migration V2: IGDB Integration Support
-- Description: Adds new tables and fields for IGDB API integration
-- Author: Professional Refactoring - FASE 5
-- Date: 2025-12-09
-- =====================================================

-- =====================================================
-- 1. CREATE NEW TABLES
-- =====================================================

-- Tabela de plataformas
CREATE TABLE tb_plataformas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) UNIQUE,
    id_igdb INT,
    abreviacao VARCHAR(20),
    tipo VARCHAR(50),
    geracao INT,
    logo VARCHAR(500),
    ativa BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id),
    INDEX idx_plataforma_slug (slug),
    INDEX idx_plataforma_tipo (tipo),
    INDEX idx_plataforma_ativa (ativa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de relacionamento Produto-Plataforma (ManyToMany)
CREATE TABLE tb_produto_plataforma (
    produto_id BIGINT NOT NULL,
    plataforma_id BIGINT NOT NULL,
    PRIMARY KEY (produto_id, plataforma_id),
    CONSTRAINT fk_prod_plat_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE,
    CONSTRAINT fk_prod_plat_plataforma FOREIGN KEY (plataforma_id) 
        REFERENCES tb_plataformas(id) ON DELETE CASCADE,
    INDEX idx_produto_plataforma (produto_id),
    INDEX idx_plataforma_produto (plataforma_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de origem externa (rastreamento de importação)
CREATE TABLE tb_produto_origem_externa (
    id BIGINT NOT NULL AUTO_INCREMENT,
    produto_id BIGINT NOT NULL UNIQUE,
    origem VARCHAR(50) NOT NULL,
    id_externo VARCHAR(100),
    url_externa VARCHAR(500),
    data_importacao DATETIME(6),
    data_ultima_sincronizacao DATETIME(6),
    versao_dados INT,
    sincronizacao_ativa BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_origem_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE,
    INDEX idx_origem_tipo (origem),
    INDEX idx_origem_id_externo (origem, id_externo),
    INDEX idx_origem_sincronizacao (sincronizacao_ativa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de imagens estruturadas
CREATE TABLE tb_produto_imagens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    produto_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    ordem INT,
    imagem_principal BOOLEAN DEFAULT FALSE,
    largura INT,
    altura INT,
    id_igdb VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_img_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE,
    INDEX idx_imagem_produto (produto_id),
    INDEX idx_imagem_tipo (tipo),
    INDEX idx_imagem_principal (imagem_principal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de vídeos
CREATE TABLE tb_produto_videos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    produto_id BIGINT NOT NULL,
    video_id VARCHAR(100) NOT NULL,
    titulo VARCHAR(255),
    tipo VARCHAR(50),
    ordem INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_video_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE,
    INDEX idx_video_produto (produto_id),
    INDEX idx_video_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de links externos
CREATE TABLE tb_produto_links (
    produto_id BIGINT NOT NULL,
    tipo_link VARCHAR(50) NOT NULL,
    url VARCHAR(500) NOT NULL,
    PRIMARY KEY (produto_id, tipo_link),
    CONSTRAINT fk_link_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de relacionamento Produto-Gênero (ManyToMany)
CREATE TABLE tb_produto_genero (
    produto_id BIGINT NOT NULL,
    genero_id BIGINT NOT NULL,
    PRIMARY KEY (produto_id, genero_id),
    CONSTRAINT fk_prod_gen_produto FOREIGN KEY (produto_id) 
        REFERENCES tb_produtos(id) ON DELETE CASCADE,
    CONSTRAINT fk_prod_gen_genero FOREIGN KEY (genero_id) 
        REFERENCES tb_categoria(id) ON DELETE CASCADE,
    INDEX idx_produto_genero (produto_id),
    INDEX idx_genero_produto (genero_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. ALTER EXISTING TABLES
-- =====================================================

-- Adicionar novos campos na tb_produtos
ALTER TABLE tb_produtos
    ADD COLUMN slug VARCHAR(100) UNIQUE AFTER nome,
    ADD COLUMN descricao_completa TEXT AFTER descricao,
    ADD COLUMN rating_igdb DECIMAL(5,2) AFTER ativo,
    ADD COLUMN rating_metacritic INT AFTER rating_igdb,
    ADD COLUMN total_votos_externos INT AFTER rating_metacritic,
    ADD COLUMN popularidade INT AFTER total_votos_externos,
    ADD COLUMN status VARCHAR(50) DEFAULT 'RELEASED' AFTER popularidade,
    ADD COLUMN data_criacao DATETIME(6) AFTER status,
    ADD COLUMN data_atualizacao DATETIME(6) AFTER data_criacao;

-- Atualizar campos existentes em tb_produtos
ALTER TABLE tb_produtos
    MODIFY COLUMN nome VARCHAR(100) NOT NULL,
    MODIFY COLUMN descricao VARCHAR(500);

-- Adicionar novos campos na tb_categoria (agora também representa gêneros)
ALTER TABLE tb_categoria
    ADD COLUMN slug VARCHAR(100) UNIQUE AFTER tipo,
    ADD COLUMN id_igdb INT AFTER slug;

-- =====================================================
-- 3. CREATE INDEXES FOR PERFORMANCE
-- =====================================================

-- Índices na tb_produtos
CREATE INDEX idx_produto_slug ON tb_produtos(slug);
CREATE INDEX idx_produto_status ON tb_produtos(status);
CREATE INDEX idx_produto_rating_igdb ON tb_produtos(rating_igdb DESC);
CREATE INDEX idx_produto_popularidade ON tb_produtos(popularidade DESC);
CREATE INDEX idx_produto_data_criacao ON tb_produtos(data_criacao);
CREATE INDEX idx_produto_ativo_status ON tb_produtos(ativo, status);

-- Índices na tb_categoria
CREATE INDEX idx_categoria_slug ON tb_categoria(slug);
CREATE INDEX idx_categoria_id_igdb ON tb_categoria(id_igdb);

-- =====================================================
-- 4. POPULATE INITIAL DATA
-- =====================================================

-- Inserir plataformas principais
INSERT INTO tb_plataformas (nome, slug, id_igdb, abreviacao, tipo, geracao, ativa) VALUES
('PlayStation 5', 'playstation-5', 167, 'PS5', 'CONSOLE', 9, TRUE),
('Xbox Series X|S', 'xbox-series-x-s', 169, 'XSX', 'CONSOLE', 9, TRUE),
('Nintendo Switch', 'nintendo-switch', 130, 'Switch', 'CONSOLE', 8, TRUE),
('PC (Windows)', 'pc-windows', 6, 'PC', 'PC', NULL, TRUE),
('PlayStation 4', 'playstation-4', 48, 'PS4', 'CONSOLE', 8, TRUE),
('Xbox One', 'xbox-one', 49, 'Xbox One', 'CONSOLE', 8, TRUE),
('iOS', 'ios', 39, 'iOS', 'MOBILE', NULL, TRUE),
('Android', 'android', 34, 'Android', 'MOBILE', NULL, TRUE),
('Mac', 'mac', 14, 'Mac', 'PC', NULL, TRUE),
('Linux', 'linux', 3, 'Linux', 'PC', NULL, TRUE),
('Steam Deck', 'steam-deck', 170, 'Deck', 'HANDHELD', NULL, TRUE);

-- Gerar slugs para categorias existentes (se houver)
UPDATE tb_categoria 
SET slug = LOWER(REPLACE(REPLACE(REPLACE(tipo, ' ', '-'), 'ã', 'a'), 'ç', 'c'))
WHERE slug IS NULL;

-- Atualizar data_criacao e data_atualizacao para produtos existentes
UPDATE tb_produtos 
SET data_criacao = NOW(), 
    data_atualizacao = NOW(),
    status = 'RELEASED'
WHERE data_criacao IS NULL;

-- Gerar slugs para produtos existentes
UPDATE tb_produtos 
SET slug = LOWER(REGEXP_REPLACE(
    REGEXP_REPLACE(nome, '[^a-zA-Z0-9\\s-]', ''),
    '\\s+', '-'
))
WHERE slug IS NULL;

-- =====================================================
-- 5. COMMENTS FOR DOCUMENTATION
-- =====================================================

ALTER TABLE tb_plataformas COMMENT = 'Armazena as plataformas de jogos (consoles, PC, mobile, etc)';
ALTER TABLE tb_produto_plataforma COMMENT = 'Relacionamento N:N entre produtos e plataformas';
ALTER TABLE tb_produto_origem_externa COMMENT = 'Rastreia a origem e sincronização de produtos importados';
ALTER TABLE tb_produto_imagens COMMENT = 'Armazena múltiplas imagens de produtos (capas, screenshots, etc)';
ALTER TABLE tb_produto_videos COMMENT = 'Armazena vídeos de produtos (trailers, gameplay, etc)';
ALTER TABLE tb_produto_links COMMENT = 'Armazena links externos de produtos (Steam, site oficial, etc)';
ALTER TABLE tb_produto_genero COMMENT = 'Relacionamento N:N entre produtos e gêneros';

-- =====================================================
-- END OF MIGRATION V2
-- =====================================================
