# Diagrama MER - Energy Games Store

Diagrama de Entidades e Relacionamentos do sistema de loja de jogos com integração IGDB.

```mermaid
erDiagram
    %% ========================================
    %% ENTIDADE PRINCIPAL: PRODUTO
    %% ========================================
    tb_produtos {
        BIGINT id PK
        VARCHAR nome
        VARCHAR slug UK
        TEXT descricao
        TEXT descricao_completa
        DECIMAL preco
        INTEGER estoque
        DECIMAL desconto
        BOOLEAN ativo
        VARCHAR plataforma
        VARCHAR desenvolvedor
        VARCHAR publisher
        DATE data_lancamento
        DECIMAL rating_igdb
        DECIMAL rating_metacritic
        INTEGER total_votos_externos
        VARCHAR status
        DATETIME data_criacao
        DATETIME data_atualizacao
        BIGINT categoria_id FK
        BIGINT usuario_id FK
    }

    %% ========================================
    %% ENTIDADES DE CATEGORIZAÇÃO
    %% ========================================
    tb_categoria {
        BIGINT id PK
        VARCHAR tipo
        VARCHAR descricao
        VARCHAR icone
        BOOLEAN ativo
        DATETIME data_criacao
        VARCHAR slug UK
        INTEGER id_igdb
    }

    tb_plataformas {
        BIGINT id PK
        VARCHAR nome UK
        VARCHAR slug UK
        INTEGER id_igdb
        VARCHAR abreviacao
        VARCHAR tipo
        INTEGER geracao
        VARCHAR logo
        BOOLEAN ativa
    }

    %% ========================================
    %% ENTIDADES DE MÍDIA (IGDB)
    %% ========================================
    tb_produto_imagens {
        BIGINT id PK
        BIGINT produto_id FK
        VARCHAR url
        VARCHAR tipo
        INTEGER ordem
        BOOLEAN imagem_principal
        INTEGER largura
        INTEGER altura
        VARCHAR id_igdb
    }

    tb_produto_videos {
        BIGINT id PK
        BIGINT produto_id FK
        VARCHAR video_id
        VARCHAR titulo
        VARCHAR tipo
        INTEGER ordem
    }

    %% ========================================
    %% RASTREAMENTO DE ORIGEM (IGDB)
    %% ========================================
    tb_produto_origem_externa {
        BIGINT id PK
        BIGINT produto_id FK UK
        VARCHAR origem
        VARCHAR id_externo
        VARCHAR url_externa
        DATETIME data_importacao
        DATETIME data_ultima_sincronizacao
        INTEGER versao_dados
        BOOLEAN sincronizacao_ativa
    }

    %% ========================================
    %% TABELA DE LINKS EXTERNOS
    %% ========================================
    tb_produto_links {
        BIGINT produto_id FK
        VARCHAR links_key
        VARCHAR links_value
    }

    %% ========================================
    %% USUÁRIOS E AUTENTICAÇÃO
    %% ========================================
    tb_usuarios {
        BIGINT id PK
        VARCHAR nome
        VARCHAR usuario UK
        VARCHAR senha
        VARCHAR foto
    }

    usuario_roles {
        BIGINT usuario_id FK
        INTEGER role
    }

    %% ========================================
    %% INTERAÇÕES DO USUÁRIO
    %% ========================================
    tb_carrinho_itens {
        BIGINT id PK
        BIGINT usuario_id FK
        BIGINT produto_id FK
        INTEGER quantidade
        DECIMAL preco_unitario
        DECIMAL desconto_unitario
        DATETIME data_adicionado
        DATETIME data_atualizacao
    }

    tb_favoritos {
        BIGINT id PK
        BIGINT usuario_id FK
        BIGINT produto_id FK
        DATETIME data_adicionado
    }

    tb_avaliacoes {
        BIGINT id PK
        INTEGER nota
        VARCHAR comentario
        DATETIME data_avaliacao
        BIGINT produto_id FK
        BIGINT usuario_id FK
    }

    %% ========================================
    %% TABELAS N:N (JUNCTION TABLES)
    %% ========================================
    tb_produto_genero {
        BIGINT produto_id FK
        BIGINT categoria_id FK
    }

    tb_produto_plataforma {
        BIGINT produto_id FK
        BIGINT plataforma_id FK
    }

    %% ========================================
    %% RELACIONAMENTOS 1:N e N:1
    %% ========================================
    
    %% Usuario → Produto (autor/criador)
    tb_usuarios ||--o{ tb_produtos : "cria"
    
    %% Categoria → Produto (relacionamento LEGADO)
    tb_categoria ||--o{ tb_produtos : "categoriza_legado"
    
    %% Produto → ProdutoImagem
    tb_produtos ||--o{ tb_produto_imagens : "possui"
    
    %% Produto → ProdutoVideo
    tb_produtos ||--o{ tb_produto_videos : "possui"
    
    %% Usuario → CarrinhoItem
    tb_usuarios ||--o{ tb_carrinho_itens : "adiciona_ao_carrinho"
    
    %% Produto → CarrinhoItem
    tb_produtos ||--o{ tb_carrinho_itens : "esta_no_carrinho"
    
    %% Usuario → Favorito
    tb_usuarios ||--o{ tb_favoritos : "favorita"
    
    %% Produto → Favorito
    tb_produtos ||--o{ tb_favoritos : "e_favoritado"
    
    %% Usuario → Avaliacao
    tb_usuarios ||--o{ tb_avaliacoes : "avalia"
    
    %% Produto → Avaliacao
    tb_produtos ||--o{ tb_avaliacoes : "recebe_avaliacao"

    %% ========================================
    %% RELACIONAMENTOS 1:1
    %% ========================================
    
    %% Produto → ProdutoOrigemExterna
    tb_produtos ||--|| tb_produto_origem_externa : "tem_origem"

    %% ========================================
    %% RELACIONAMENTOS N:N
    %% ========================================
    
    %% Produto ←→ Categoria (generos IGDB)
    tb_produtos }o--o{ tb_categoria : "tem_generos"
    tb_produto_genero }o--|| tb_produtos : ""
    tb_produto_genero }o--|| tb_categoria : ""
    
    %% Produto ←→ Plataforma
    tb_produtos }o--o{ tb_plataformas : "disponivel_em"
    tb_produto_plataforma }o--|| tb_produtos : ""
    tb_produto_plataforma }o--|| tb_plataformas : ""

    %% ========================================
    %% RELACIONAMENTOS ELEMENTCOLLECTION
    %% ========================================
    
    %% Usuario → Roles (ElementCollection)
    tb_usuarios ||--o{ usuario_roles : "possui_roles"
    
    %% Produto → LinksExternos (ElementCollection)
    tb_produtos ||--o{ tb_produto_links : "possui_links"
```

## 📋 Legenda

### Cardinalidades
- `||--o{` : Um para muitos (1:N)
- `||--||` : Um para um (1:1)
- `}o--o{` : Muitos para muitos (N:N)

### Tipos de Relacionamentos

#### 🔵 Relacionamentos Principais do Produto
1. **Produto ← Usuario** (N:1): Usuário que cadastrou/criou o produto
2. **Produto ← Categoria** (N:1): Relacionamento LEGADO para categorização antiga
3. **Produto → ProdutoOrigemExterna** (1:1): Rastreamento de importação IGDB
4. **Produto → ProdutoImagem** (1:N): Capa + screenshots importadas da IGDB
5. **Produto → ProdutoVideo** (1:N): Trailers do YouTube importados da IGDB

#### 🟢 Relacionamentos N:N (IGDB Integration)
6. **Produto ←→ Categoria (generos)** (N:N): Gêneros importados da IGDB (Action, RPG, etc)
7. **Produto ←→ Plataforma** (N:N): Plataformas onde o jogo está disponível (PS5, Xbox, PC)

#### 🟡 Relacionamentos de Interação do Usuário
8. **Usuario → CarrinhoItem ← Produto** (N:N): Carrinho de compras
9. **Usuario → Favorito ← Produto** (N:N): Lista de favoritos
10. **Usuario → Avaliacao ← Produto** (N:N): Avaliações e comentários

#### 🟠 ElementCollection (Tabelas Auxiliares)
11. **Produto → LinksExternos**: Map<String, String> para links Steam, Epic Games, etc
12. **Usuario → Roles**: Set<RoleEnum> convertido para INTEGER (0=USER, 1=ADMIN)

## 🔍 Entidades Principais

### 🎮 `tb_produtos`
Entidade central do sistema com campos comerciais e de integração IGDB.

**Campos Comerciais:**
- `preco`: Preço em R$ (não vem da IGDB)
- `estoque`: Quantidade em estoque (não vem da IGDB)
- `desconto`: Percentual de desconto
- `ativo`: Produto ativo/inativo para venda

**Campos IGDB:**
- `rating_igdb`: Nota da IGDB (0-100)
- `rating_metacritic`: Nota do Metacritic (0-100)
- `status`: Status do jogo (released, early_access, etc)
- `total_votos_externos`: Total de avaliações

### 🏷️ `tb_categoria`
Tabela com **dois propósitos**:
1. **Categorização Legada**: Relacionamento N:1 com Produto (campo `categoria_id`)
2. **Gêneros IGDB**: Relacionamento N:N através de `tb_produto_genero`

### 🎯 `tb_plataformas`
Plataformas de jogos (PS5, Xbox, PC, Switch, etc) importadas da IGDB.
- Relacionamento N:N com Produto através de `tb_produto_plataforma`

### 📸 `tb_produto_imagens`
Imagens do produto importadas da IGDB:
- `tipo`: CAPA, SCREENSHOT, ARTWORK, LOGO
- `id_igdb`: Rastreamento do image_id da IGDB
- `imagem_principal`: Flag para capa do jogo

### 🎬 `tb_produto_videos`
Vídeos do YouTube (trailers, gameplay) importados da IGDB:
- `video_id`: ID do vídeo no YouTube
- `tipo`: TRAILER, GAMEPLAY, REVIEW

### 🌐 `tb_produto_origem_externa`
Rastreamento de produtos importados de APIs externas (principalmente IGDB):
- `origem`: Enum (IGDB, MANUAL, RAWG)
- `id_externo`: ID do jogo na API externa (IGDB game ID)
- `data_ultima_sincronizacao`: Controle de atualizações periódicas
- `sincronizacao_ativa`: Se deve atualizar periodicamente

### 👤 `tb_usuarios`
Usuários do sistema com autenticação JWT e controle de roles.

### 🔐 `usuario_roles`
ElementCollection que armazena roles do usuário como INTEGER:
- `0` = USER (ROLE_USER)
- `1` = ADMIN (ROLE_ADMIN)

Conversão via `RoleEnumConverter`.

## 🔄 Fluxo de Importação IGDB

1. **Admin busca jogo**: `GET /admin/igdb/search?nome=zelda`
2. **Admin importa**: `POST /admin/igdb/import/{igdbId}`
3. **Sistema cria**:
   - ✅ `tb_produtos` (price=59.99, stock=0, active=false)
   - ✅ `tb_produto_origem_externa` (origem=IGDB, idExterno=igdbId)
   - ✅ `tb_produto_imagens` (capa + screenshots)
   - ✅ `tb_produto_videos` (trailers)
   - ✅ `tb_produto_genero` (N:N com categorias)
   - ✅ `tb_produto_plataforma` (N:N com plataformas)
   - ✅ `tb_produto_links` (Steam, Epic Games)
4. **Admin configura**: `PATCH /produtos/{id}/comercial` (seta preço real, estoque, ativa produto)

## 📊 Estatísticas do MER

- **Total de Entidades**: 11 tabelas principais
- **Total de Tabelas Auxiliares**: 4 (junction tables + ElementCollection)
- **Relacionamentos 1:1**: 1 (Produto ←→ ProdutoOrigemExterna)
- **Relacionamentos 1:N**: 8
- **Relacionamentos N:N**: 4 (com 2 junction tables + 2 ElementCollection)
- **Integração IGDB**: 6 entidades envolvidas

## 🎯 Pontos-Chave de Design

1. **Categoria Dual**: Serve tanto como categoria legada (N:1) quanto como gênero IGDB (N:N)
2. **Origem Externa**: Rastreia todos os produtos importados para controle de sincronização
3. **Mídia Estruturada**: Imagens e vídeos em tabelas separadas com metadados
4. **Links Externos**: ElementCollection Map para flexibilidade (Steam, Epic, GOG, etc)
5. **Roles como INTEGER**: Conversão automática via AttributeConverter
6. **UNIQUE Constraints**: `usuario_id + produto_id` em CarrinhoItem e Favorito

