# 🔄 Fluxo Detalhado: API IGDB → Banco de Dados

## 📊 Arquitetura de Dados

### **Entidades Persistidas no Banco**

```
tb_produtos (Tabela Principal)
├── Campos básicos: id, nome, slug, descricao, descricaoCompleta
├── Campos comerciais: preco, estoque, desconto, ativo
├── Campos IGDB: ratingIgdb, ratingMetacritic, totalVotosExternos
├── Status: status (RELEASED, EARLY_ACCESS, etc)
├── Data: dataLancamento, dataCriacao, dataAtualizacao
└── Relacionamentos:
    ├── 1:N → tb_produto_imagens
    ├── 1:N → tb_produto_videos
    ├── 1:1 → tb_produto_origem_externa
    ├── N:N → tb_produto_genero (relacionamento com categorias)
    ├── N:N → tb_produto_plataforma
    └── 1:N → tb_produto_links (Map de links externos)

tb_produto_origem_externa
├── id, origem (IGDB), id_externo (ID na IGDB)
├── url_externa (link para IGDB)
├── sincronizacao_ativa (boolean)
├── data_ultima_sincronizacao
└── produto_id (FK)

tb_produto_imagens
├── id, url, tipo (CAPA/SCREENSHOT)
├── imagem_principal (boolean)
├── ordem, largura, altura
├── id_igdb (ID da imagem na IGDB)
└── produto_id (FK)

tb_produto_videos
├── id, video_id (YouTube ID)
├── titulo, tipo (TRAILER)
├── ordem
└── produto_id (FK)

tb_categorias (Gêneros)
├── id, tipo, slug
├── descricao, ativo
├── id_igdb (ID do gênero na IGDB)
└── Relacionamento N:N com produtos

tb_plataformas
├── id, nome, slug
├── tipo_plataforma, ativo
├── id_igdb (ID da plataforma na IGDB)
└── Relacionamento N:N com produtos
```

---

## 🔄 Fluxo Completo de Importação

### **Etapa 1: Busca na IGDB**

```
Admin → Frontend
  └─→ GET /admin/igdb/search?nome=zelda&limit=10
        │
        └─→ IgdbAdminController.searchGames()
              │
              └─→ IgdbImportService.searchGamesForImport()
                    │
                    └─→ IgdbApiClient.searchGamesByName()
                          │
                          ├─→ POST https://api.igdb.com/v4/games
                          │   Header: Authorization: Bearer {token}
                          │   Body: search "zelda"; fields *; limit 10;
                          │
                          └─→ Retorna: List<IgdbGameDTO>
                                • id, name, slug, summary
                                • first_release_date, rating
                                • cover (ID), screenshots (IDs)
                                • platforms (IDs), genres (IDs)
```

**Dados da API IGDB (JSON):**
```json
{
  "id": 1234,
  "name": "The Legend of Zelda: Breath of the Wild",
  "slug": "the-legend-of-zelda-breath-of-the-wild",
  "summary": "Explore um vasto mundo aberto...",
  "storyline": "Link desperta após 100 anos...",
  "first_release_date": 1488499200,
  "rating": 97.5,
  "cover": 5678,
  "screenshots": [9012, 3456],
  "videos": [7890],
  "platforms": [48, 130],
  "genres": [12, 31]
}
```

---

### **Etapa 2: Admin Escolhe Jogo para Importar**

```
Admin clica "Importar"
  └─→ POST /admin/igdb/import/1234
        │
        └─→ IgdbAdminController.importGameById(1234)
              │
              └─→ IgdbImportService.importGameById(1234)
```

---

### **Etapa 3: Busca Dados Completos na IGDB**

```java
// 1. Verifica se já foi importado
ProdutoOrigemExterna existing = origemExternaRepository
    .findByOrigemAndIdExterno(OrigemEnum.IGDB, "1234");

if (existing.isPresent()) {
    return existing.get().getProduto(); // Já existe, não importa de novo
}

// 2. Busca dados do jogo principal
IgdbGameDTO gameDTO = apiClient.getGameById(1234);
// Query: fields *; where id = 1234;

// 3. Busca CAPA do jogo (se existir)
IgdbCoverDTO coverDTO = apiClient.getCoverById(gameDTO.getCover());
// Query: fields *; where id = 5678;
// Retorna: { id: 5678, image_id: "co2abc", width: 264, height: 374 }

// 4. Busca SCREENSHOTS (se existirem)
List<IgdbScreenshotDTO> screenshots = apiClient.getScreenshotsByIds([9012, 3456]);
// Query: fields *; where id = (9012,3456);
// Retorna: [{ id: 9012, image_id: "sc3def", width: 1920, height: 1080 }, ...]

// 5. Busca VÍDEOS (se existirem)
List<IgdbVideoDTO> videos = apiClient.getVideosByIds([7890]);
// Query: fields *; where id = (7890);
// Retorna: [{ id: 7890, video_id: "dQw4w9WgXcQ", name: "Official Trailer" }]

// 6. Busca PLATAFORMAS
List<IgdbPlatformDTO> platforms = apiClient.getPlatformsByIds([48, 130]);
// Query: fields *; where id = (48,130);
// Retorna: [{ id: 48, name: "PlayStation 4", slug: "ps4" }, 
//           { id: 130, name: "Nintendo Switch", slug: "switch" }]

// 7. Busca GÊNEROS
List<IgdbGenreDTO> genres = apiClient.getGenresByIds([12, 31]);
// Query: fields *; where id = (12,31);
// Retorna: [{ id: 12, name: "Role-playing (RPG)", slug: "role-playing-rpg" },
//           { id: 31, name: "Adventure", slug: "adventure" }]
```

**Total de Requisições à API IGDB: 6 chamadas**
- 1x `/games` (jogo principal)
- 1x `/covers` (capa)
- 1x `/screenshots` (imagens)
- 1x `/game_videos` (vídeos)
- 1x `/platforms` (plataformas)
- 1x `/genres` (gêneros)

---

### **Etapa 4: Mapeamento para Entidades JPA**

```java
// IgdbMapperService.mapGameToProduct()

Produto produto = new Produto();

// 4.1 - Campos Básicos
produto.setNome("The Legend of Zelda: Breath of the Wild");
produto.setSlug("the-legend-of-zelda-breath-of-the-wild");
produto.setDescricao("Explore um vasto mundo aberto..."); // 500 chars max
produto.setDescricaoCompleta("Link desperta após 100 anos..."); // sem limite

// 4.2 - Data de Lançamento (Unix timestamp → LocalDate)
LocalDate data = Instant.ofEpochSecond(1488499200)
    .atZone(ZoneId.systemDefault())
    .toLocalDate(); // 2017-03-03
produto.setDataLancamento(data);

// 4.3 - Ratings
produto.setRatingIgdb(BigDecimal.valueOf(9.75)); // 97.5 / 10
produto.setTotalVotosExternos(1250);

// 4.4 - Status
produto.setStatus(StatusJogoEnum.RELEASED);

// 4.5 - Campos Comerciais (PADRÃO)
produto.setPreco(new BigDecimal("59.99")); // Preço padrão
produto.setEstoque(0); // Estoque zerado
produto.setDesconto(BigDecimal.ZERO);
produto.setAtivo(false); // INATIVO até admin configurar

// 4.6 - Campos Genéricos
produto.setPlataforma("Multiplataforma");
produto.setDesenvolvedor("A definir");
produto.setPublisher("A definir");

// 4.7 - Origem Externa
ProdutoOrigemExterna origem = new ProdutoOrigemExterna();
origem.setProduto(produto);
origem.setOrigem(OrigemEnum.IGDB);
origem.setIdExterno("1234");
origem.setUrlExterna("https://www.igdb.com/games/the-legend-of-zelda-breath-of-the-wild");
origem.setSincronizacaoAtiva(true);
produto.setOrigemExterna(origem);

// 4.8 - Imagens (Capa + Screenshots)
Set<ProdutoImagem> imagens = new HashSet<>();

// CAPA
ProdutoImagem capa = new ProdutoImagem();
capa.setProduto(produto);
capa.setUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co2abc.jpg");
capa.setTipo(TipoImagemEnum.CAPA);
capa.setImagemPrincipal(true);
capa.setOrdem(1);
capa.setLargura(264);
capa.setAltura(374);
capa.setIdIgdb("5678");
imagens.add(capa);

// SCREENSHOT 1
ProdutoImagem screenshot1 = new ProdutoImagem();
screenshot1.setProduto(produto);
screenshot1.setUrl("https://images.igdb.com/igdb/image/upload/t_screenshot_big/sc3def.jpg");
screenshot1.setTipo(TipoImagemEnum.SCREENSHOT);
screenshot1.setImagemPrincipal(false);
screenshot1.setOrdem(2);
screenshot1.setLargura(1920);
screenshot1.setAltura(1080);
screenshot1.setIdIgdb("9012");
imagens.add(screenshot1);

produto.setImagensEstruturadas(imagens);

// 4.9 - Vídeos
Set<ProdutoVideo> videos = new HashSet<>();

ProdutoVideo video = new ProdutoVideo();
video.setProduto(produto);
video.setVideoId("dQw4w9WgXcQ"); // YouTube ID
video.setTitulo("Official Trailer");
video.setTipo(TipoVideoEnum.TRAILER);
video.setOrdem(1);
videos.add(video);

produto.setVideos(videos);

// 4.10 - Links Externos (Steam, Epic, etc)
Map<String, String> links = new HashMap<>();
links.put("Steam", "https://store.steampowered.com/app/123456");
links.put("Epic Games", "https://store.epicgames.com/...");
links.put("Site Oficial", "https://zelda.nintendo.com");
produto.setLinksExternos(links);

// 4.11 - Plataformas (busca no banco por idIgdb)
Set<Plataforma> plataformas = new HashSet<>();

// PlayStation 4 (id_igdb = 48)
Optional<Plataforma> ps4 = plataformaRepository.findByIdIgdb(48);
if (ps4.isPresent()) {
    plataformas.add(ps4.get());
}

// Nintendo Switch (id_igdb = 130)
Optional<Plataforma> switchPlat = plataformaRepository.findByIdIgdb(130);
if (switchPlat.isPresent()) {
    plataformas.add(switchPlat.get());
}

produto.setPlataformas(plataformas);

// 4.12 - Gêneros (busca ou cria no banco)
Set<Categoria> generos = new HashSet<>();

// RPG (id_igdb = 12)
Optional<Categoria> rpg = categoriaRepository.findByIdIgdb(12);
if (rpg.isPresent()) {
    generos.add(rpg.get());
} else {
    // Cria nova categoria
    Categoria novaCategoria = new Categoria();
    novaCategoria.setTipo("Role-playing (RPG)");
    novaCategoria.setSlug("role-playing-rpg");
    novaCategoria.setIdIgdb(12);
    novaCategoria.setDescricao("Gênero importado da IGDB");
    novaCategoria.setAtivo(true);
    generos.add(novaCategoria);
}

// Adventure (id_igdb = 31)
Optional<Categoria> adventure = categoriaRepository.findByIdIgdb(31);
if (adventure.isPresent()) {
    generos.add(adventure.get());
}

produto.setGeneros(generos);
```

---

### **Etapa 5: Persistência no Banco de Dados**

```java
// 1. Salva categorias novas primeiro (se necessário)
for (Categoria categoria : produto.getGeneros()) {
    if (categoria.getId() == null) { // Nova categoria
        categoriaRepository.save(categoria);
    }
}

// 2. Salva produto (Cascade persiste tudo automaticamente)
Produto savedProduto = produtoRepository.save(produto);

// SQL gerados automaticamente pelo Hibernate:

// INSERT INTO tb_produtos (nome, slug, descricao, preco, estoque, ativo, ...)
// VALUES ('The Legend of Zelda...', 'the-legend...', 'Explore...', 59.99, 0, false, ...);

// INSERT INTO tb_produto_origem_externa (produto_id, origem, id_externo, ...)
// VALUES (42, 'IGDB', '1234', ...);

// INSERT INTO tb_produto_imagens (produto_id, url, tipo, ordem, ...)
// VALUES (42, 'https://images.igdb.com/...', 'CAPA', 1, ...);

// INSERT INTO tb_produto_imagens (produto_id, url, tipo, ordem, ...)
// VALUES (42, 'https://images.igdb.com/...', 'SCREENSHOT', 2, ...);

// INSERT INTO tb_produto_videos (produto_id, video_id, titulo, ...)
// VALUES (42, 'dQw4w9WgXcQ', 'Official Trailer', ...);

// INSERT INTO tb_produto_links (produto_id, tipo_link, url)
// VALUES (42, 'Steam', 'https://store.steampowered.com/...');

// INSERT INTO tb_produto_genero (produto_id, genero_id)
// VALUES (42, 5), (42, 8);

// INSERT INTO tb_produto_plataforma (produto_id, plataforma_id)
// VALUES (42, 12), (42, 23);
```

---

## 📦 Estrutura de Dados Persistida

### **Exemplo Completo no Banco:**

```sql
-- tb_produtos (ID: 42)
+----+------------------------------------------------+--------+---------+---------+
| id | nome                                           | preco  | estoque | ativo   |
+----+------------------------------------------------+--------+---------+---------+
| 42 | The Legend of Zelda: Breath of the Wild        | 59.99  | 0       | false   |
+----+------------------------------------------------+--------+---------+---------+

-- tb_produto_origem_externa
+----+------------+-----------+--------------------------------------------------+
| id | produto_id | id_externo| url_externa                                      |
+----+------------+-----------+--------------------------------------------------+
| 15 | 42         | 1234      | https://www.igdb.com/games/the-legend-of...     |
+----+------------+-----------+--------------------------------------------------+

-- tb_produto_imagens
+----+------------+-------------------------------------------------------+--------+
| id | produto_id | url                                                   | tipo   |
+----+------------+-------------------------------------------------------+--------+
| 80 | 42         | https://images.igdb.com/.../co2abc.jpg              | CAPA   |
| 81 | 42         | https://images.igdb.com/.../sc3def.jpg              | SCREEN |
| 82 | 42         | https://images.igdb.com/.../sc4ghi.jpg              | SCREEN |
+----+------------+-------------------------------------------------------+--------+

-- tb_produto_videos
+----+------------+--------------+-------------------+
| id | produto_id | video_id     | titulo            |
+----+------------+--------------+-------------------+
| 25 | 42         | dQw4w9WgXcQ  | Official Trailer  |
+----+------------+--------------+-------------------+

-- tb_produto_links
+------------+--------------+----------------------------------------+
| produto_id | tipo_link    | url                                    |
+------------+--------------+----------------------------------------+
| 42         | Steam        | https://store.steampowered.com/...     |
| 42         | Epic Games   | https://store.epicgames.com/...        |
| 42         | Site Oficial | https://zelda.nintendo.com             |
+------------+--------------+----------------------------------------+

-- tb_produto_genero (relacionamento N:N)
+------------+-----------+
| produto_id | genero_id |
+------------+-----------+
| 42         | 5         | -- Role-playing (RPG)
| 42         | 8         | -- Adventure
+------------+-----------+

-- tb_produto_plataforma (relacionamento N:N)
+------------+---------------+
| produto_id | plataforma_id |
+------------+---------------+
| 42         | 12            | -- PlayStation 4
| 42         | 23            | -- Nintendo Switch
+------------+---------------+
```

---

## 🎯 Campos que NÃO vêm da IGDB (Admin define depois)

```java
// Estes campos são setados com valores padrão:
produto.setPreco(new BigDecimal("59.99")); // Admin ajusta depois
produto.setEstoque(0); // Admin define estoque
produto.setAtivo(false); // Admin ativa após revisar
produto.setDesconto(BigDecimal.ZERO); // Admin define promoções
produto.setPlataforma("Multiplataforma"); // Genérico
produto.setDesenvolvedor("A definir"); // Pode buscar na IGDB depois
produto.setPublisher("A definir"); // Pode buscar na IGDB depois
```

**Admin usa endpoint:**
```http
PATCH /produtos/42/comercial
{
  "preco": 199.90,
  "estoque": 50,
  "desconto": 10,
  "ativo": true
}
```

---

## 🔄 Sincronização Automática

Produtos com `sincronizacao_ativa = true` são atualizados periodicamente:

```java
// Executado a cada 7 dias (configurável)
@Scheduled(cron = "0 0 2 * * *") // 2h da manhã
public void syncOutdatedProducts() {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
    
    List<ProdutoOrigemExterna> outdated = origemExternaRepository
        .findProdutosParaSincronizar(OrigemEnum.IGDB, cutoffDate);
    
    for (ProdutoOrigemExterna origem : outdated) {
        // Re-busca dados na IGDB
        IgdbGameDTO gameDTO = apiClient.getGameById(Long.parseLong(origem.getIdExterno()));
        
        // Atualiza apenas campos não-comerciais
        // (mantém preço, estoque, desconto definidos pelo admin)
        Produto produto = origem.getProduto();
        produto.setDescricao(gameDTO.getSummary());
        produto.setRatingIgdb(gameDTO.getTotalRating() / 10);
        // ... atualiza ratings, imagens, etc
        
        origem.setDataUltimaSincronizacao(LocalDateTime.now());
        produtoRepository.save(produto);
    }
}
```

---

## 📊 Resumo: O que é Buscado vs O que é Persistido

| Dado da IGDB | Campo no Banco | Observações |
|--------------|----------------|-------------|
| `id` | `produto_origem_externa.id_externo` | Referência para sincronização |
| `name` | `produtos.nome` | Nome do jogo |
| `slug` | `produtos.slug` | URL amigável |
| `summary` | `produtos.descricao` | Descrição curta (500 chars) |
| `storyline` | `produtos.descricaoCompleta` | Descrição longa (sem limite) |
| `first_release_date` | `produtos.dataLancamento` | Convertido de Unix para LocalDate |
| `rating` | `produtos.ratingIgdb` | Convertido para escala 0-10 |
| `cover.image_id` | `produto_imagens.url` | URL construída com image_id |
| `screenshots[].image_id` | `produto_imagens.url` | Múltiplos screenshots |
| `videos[].video_id` | `produto_videos.video_id` | IDs do YouTube |
| `platforms[].id` | `produto_plataforma.plataforma_id` | Relacionamento N:N |
| `genres[].id` | `produto_genero.genero_id` | Relacionamento N:N |
| `external_games` | `produto_links.url` | Links Steam, Epic, etc |
| `websites` | `produto_links.url` | Site oficial, redes sociais |
| ❌ **Preço** | `produtos.preco` | **ADMIN define** (R$ 59,99 padrão) |
| ❌ **Estoque** | `produtos.estoque` | **ADMIN define** (0 padrão) |
| ❌ **Ativo** | `produtos.ativo` | **ADMIN ativa** (false padrão) |

---

## 🚀 Performance e Otimizações

### **Cache de Tokens OAuth2**
```java
// Token é reutilizado por 60 dias
@Cacheable("igdb-token")
public String getAccessToken() {
    // Busca apenas quando expira
}
```

### **Batch de Requisições**
```java
// Busca múltiplos IDs em uma única requisição
String query = "fields *; where id = (9012,3456,7890);";
// Ao invés de 3 requisições separadas
```

### **Lazy Loading**
```java
@OneToMany(fetch = FetchType.LAZY)
private Set<ProdutoImagem> imagensEstruturadas;
// Imagens só são carregadas quando necessário
```

---

## ❓ Perguntas Frequentes

**Q: O que acontece se a IGDB mudar os dados do jogo?**  
A: Sincronização automática atualiza produtos a cada 7 dias (configurável).

**Q: Posso desativar a sincronização de um produto específico?**  
A: Sim, via `PUT /admin/igdb/sync/toggle/{produtoId}?ativo=false`

**Q: E se o jogo já foi importado?**  
A: Sistema detecta e retorna o produto existente, sem duplicar.

**Q: Posso importar jogos sem capa ou screenshots?**  
A: Sim, todos os campos relacionados são opcionais (verificações `!= null`).

**Q: Como sei quais plataformas/gêneros existem no banco?**  
A: Deve haver uma migration seed com plataformas/gêneros da IGDB pré-cadastrados.

---

**✅ Fluxo garantido: Máximo de informação da IGDB, mínimo trabalho manual do admin!**
