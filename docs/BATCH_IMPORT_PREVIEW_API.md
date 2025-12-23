# API de Seleção em Lotes, Preview Rápido e Ordenação - IGDB Admin

## Visão Geral

Esta documentação descreve as funcionalidades implementadas para melhorar a experiência do administrador ao gerenciar a importação de jogos da IGDB:

1. **Seleção em Lotes**: Importar múltiplos jogos simultaneamente
2. **Preview Rápido**: Visualizar detalhes completos sem importar
3. **Ordenação**: Ordenar resultados de busca por diferentes critérios
4. **Feedback Visual**: Respostas detalhadas com status e progresso

## Endpoints

### 1. Importação em Lote

#### `POST /admin/igdb/import/batch`

Importa múltiplos jogos da IGDB em uma única requisição.

**Autenticação**: Bearer Token (Role: ADMIN)

**Request Body**:
```json
{
  "igdbIds": [1234, 5678, 9012]
}
```

**Validações**:
- Mínimo: 1 ID
- Máximo: 50 IDs por requisição
- IDs devem ser números válidos da IGDB

**Response** (200 OK):
```json
{
  "totalSolicitado": 3,
  "totalProcessado": 3,
  "sucessos": 2,
  "jaImportados": 1,
  "falhas": 0,
  "dataExecucao": "2025-12-15T23:45:00",
  "status": "CONCLUIDO",
  "mensagemResumo": "Processados 3 de 3 jogos: 2 importados com sucesso, 1 já existentes, 0 falhas",
  "resultados": [
    {
      "sucesso": true,
      "mensagem": "Jogo importado com sucesso",
      "produtoId": 101,
      "nomeProduto": "The Legend of Zelda: Breath of the Wild",
      "igdbId": 1234,
      "dataImportacao": "2025-12-15T23:45:01"
    },
    {
      "sucesso": true,
      "mensagem": "Jogo já estava importado",
      "produtoId": 85,
      "nomeProduto": "Super Mario Odyssey",
      "igdbId": 5678
    },
    {
      "sucesso": true,
      "mensagem": "Jogo importado com sucesso",
      "produtoId": 102,
      "nomeProduto": "God of War",
      "igdbId": 9012,
      "dataImportacao": "2025-12-15T23:45:03"
    }
  ]
}
```

**Response com Falhas** (200 OK - Processamento Parcial):
```json
{
  "totalSolicitado": 3,
  "totalProcessado": 3,
  "sucessos": 2,
  "jaImportados": 0,
  "falhas": 1,
  "dataExecucao": "2025-12-15T23:45:00",
  "status": "PARCIAL",
  "mensagemResumo": "Processados 3 de 3 jogos: 2 importados com sucesso, 0 já existentes, 1 falhas",
  "resultados": [
    {
      "sucesso": true,
      "mensagem": "Jogo importado com sucesso",
      "produtoId": 101,
      "nomeProduto": "The Legend of Zelda: Breath of the Wild",
      "igdbId": 1234
    },
    {
      "sucesso": false,
      "mensagem": "Erro na importação",
      "erro": "Falha ao importar IGDB ID: 9999"
    },
    {
      "sucesso": true,
      "mensagem": "Jogo importado com sucesso",
      "produtoId": 102,
      "nomeProduto": "God of War",
      "igdbId": 9012
    }
  ]
}
```

**Possíveis Status**:
- `CONCLUIDO`: Todos os jogos foram processados com sucesso
- `PARCIAL`: Alguns jogos foram importados, outros falharam
- `ERRO`: Falha completa no processamento

---

### 2. Preview Rápido de Jogo

#### `GET /admin/igdb/preview/{igdbId}`

Obtém informações detalhadas de um jogo da IGDB sem importá-lo.

**Autenticação**: Bearer Token (Role: ADMIN)

**Path Parameters**:
- `igdbId` (number, required): ID do jogo na IGDB

**Response** (200 OK):
```json
{
  "igdbId": 1234,
  "nome": "The Legend of Zelda: Breath of the Wild",
  "slug": "the-legend-of-zelda-breath-of-the-wild",
  "descricao": "Uma aventura épica em mundo aberto...",
  "storyline": "Link desperta após 100 anos de sono...",
  "dataLancamento": "2017-03-03",
  "rating": 95.5,
  "ratingCount": 2500,
  "urlCapa": "https://images.igdb.com/igdb/image/upload/t_cover_big/co1234.jpg",
  "screenshots": [
    "https://images.igdb.com/igdb/image/upload/t_screenshot_big/sc1001.jpg",
    "https://images.igdb.com/igdb/image/upload/t_screenshot_big/sc1002.jpg"
  ],
  "videos": ["dQw4w9WgXcQ", "oHg5SJYRHA0"],
  "plataformas": ["Nintendo Switch", "Wii U"],
  "generos": ["Role-playing (RPG)", "Adventure"],
  "desenvolvedores": "Nintendo EPD",
  "publishers": "Nintendo",
  "jaImportado": false,
  "produtoIdLocal": null
}
```

**Response** (404 Not Found):
```json
{
  "timestamp": "2025-12-15T23:45:00",
  "status": 404,
  "error": "Not Found",
  "message": "Jogo não encontrado",
  "path": "/admin/igdb/preview/99999"
}
```

---

### 3. Busca com Ordenação

#### `GET /admin/igdb/search`

Busca jogos na IGDB com suporte a ordenação personalizada.

**Autenticação**: Bearer Token (Role: ADMIN)

**Query Parameters**:
- `nome` (string, optional): Nome do jogo para buscar
- `page` (number, default: 1): Página atual
- `limit` (number, default: 20): Jogos por página
- `sortBy` (string, default: "rating"): Campo de ordenação
  - Opções: `nome`, `rating`, `datalancamento`, `importado`
- `sortDir` (string, default: "desc"): Direção da ordenação
  - Opções: `asc`, `desc`

**Exemplos de Uso**:

1. **Buscar por nome e ordenar por rating (descendente)**:
   ```
   GET /admin/igdb/search?nome=zelda&sortBy=rating&sortDir=desc
   ```

2. **Ordenar por nome alfabeticamente**:
   ```
   GET /admin/igdb/search?nome=mario&sortBy=nome&sortDir=asc
   ```

3. **Ordenar por data de lançamento (mais recentes primeiro)**:
   ```
   GET /admin/igdb/search?sortBy=datalancamento&sortDir=desc
   ```

4. **Mostrar jogos não importados primeiro**:
   ```
   GET /admin/igdb/search?nome=rpg&sortBy=importado&sortDir=asc
   ```

**Response** (200 OK):
```json
[
  {
    "igdbId": 1234,
    "nome": "The Legend of Zelda: Breath of the Wild",
    "slug": "the-legend-of-zelda-breath-of-the-wild",
    "descricao": "Uma aventura épica...",
    "dataLancamento": "2017-03-03",
    "rating": 95.5,
    "urlCapa": "https://images.igdb.com/igdb/image/upload/t_cover_big/co1234.jpg",
    "plataformas": ["Nintendo Switch", "Wii U"],
    "generos": ["Role-playing (RPG)", "Adventure"],
    "jaImportado": false,
    "produtoIdLocal": null
  },
  {
    "igdbId": 5678,
    "nome": "The Legend of Zelda: Ocarina of Time",
    "slug": "the-legend-of-zelda-ocarina-of-time",
    "descricao": "O clássico atemporal...",
    "dataLancamento": "1998-11-21",
    "rating": 98.0,
    "urlCapa": "https://images.igdb.com/igdb/image/upload/t_cover_big/co5678.jpg",
    "plataformas": ["Nintendo 64"],
    "generos": ["Role-playing (RPG)", "Adventure"],
    "jaImportado": true,
    "produtoIdLocal": 42
  }
]
```

---

## Casos de Uso

### Caso 1: Importar Múltiplos Jogos Selecionados

1. Buscar jogos: `GET /admin/igdb/search?nome=zelda`
2. Selecionar IDs dos jogos desejados: `[1234, 5678, 9012]`
3. Importar em lote: `POST /admin/igdb/import/batch` com os IDs
4. Receber feedback detalhado com status de cada importação

### Caso 2: Preview Antes de Importar

1. Buscar jogos: `GET /admin/igdb/search?nome=mario`
2. Ver preview de um jogo específico: `GET /admin/igdb/preview/1234`
3. Avaliar detalhes (screenshots, descrição, rating)
4. Decidir se importa: `POST /admin/igdb/import/1234` ou cancelar

### Caso 3: Encontrar Jogos Não Importados com Alto Rating

1. Buscar: `GET /admin/igdb/search?sortBy=rating&sortDir=desc&limit=50`
2. Filtrar visualmente jogos com `jaImportado: false`
3. Ou buscar e ordenar: `GET /admin/igdb/search?sortBy=importado&sortDir=asc`
4. Selecionar os melhores IDs e importar em lote

---

## Feedback Visual

### Indicadores de Status na Resposta

Todas as operações retornam informações claras sobre o resultado:

**Importação Individual**:
- ✅ `sucesso: true` - Operação bem-sucedida
- ❌ `sucesso: false` - Falha na operação
- `mensagem` - Descrição do resultado
- `erro` - Detalhes do erro (quando aplicável)

**Importação em Lote**:
- `status`: CONCLUIDO/PARCIAL/ERRO
- `mensagemResumo`: Resumo textual dos resultados
- Contadores: `sucessos`, `jaImportados`, `falhas`
- Timestamps: `dataExecucao`, `dataImportacao`

**Busca**:
- `jaImportado`: Indica se o jogo já está no sistema
- `produtoIdLocal`: ID do produto local (se importado)
- Dados completos para decisão de importação

---

## Boas Práticas

### Importação em Lote

1. **Limite Recomendado**: 10-20 jogos por lote para melhor performance
2. **Tratamento de Erros**: Sempre verificar o campo `status` e `resultados`
3. **Retry**: Para jogos que falharam, tentar novamente individualmente

### Preview

1. **Cache do Frontend**: Cachear previews visualizados recentemente
2. **Lazy Loading**: Carregar screenshots sob demanda
3. **Validação**: Verificar `jaImportado` antes de mostrar botão de importar

### Ordenação

1. **Rating**: Use para encontrar os melhores jogos
2. **Nome**: Use para navegação alfabética
3. **Data Lançamento**: Use para encontrar lançamentos recentes
4. **Status Importado**: Use para identificar lacunas no catálogo

---

## Códigos de Resposta HTTP

| Código | Descrição |
|--------|-----------|
| 200 | Operação bem-sucedida |
| 400 | Dados de entrada inválidos (validação falhou) |
| 401 | Não autenticado |
| 403 | Não autorizado (requer role ADMIN) |
| 404 | Recurso não encontrado (preview de jogo inexistente) |
| 500 | Erro interno do servidor |

---

## Exemplos de Integração

### JavaScript/TypeScript

```typescript
// Importar em lote
async function batchImport(igdbIds: number[]) {
  const response = await fetch('/admin/igdb/import/batch', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ igdbIds })
  });
  
  const result = await response.json();
  
  if (result.status === 'CONCLUIDO') {
    console.log(`✅ Todos os ${result.sucessos} jogos importados!`);
  } else if (result.status === 'PARCIAL') {
    console.log(`⚠️  ${result.sucessos} sucessos, ${result.falhas} falhas`);
  } else {
    console.error(`❌ Erro na importação: ${result.mensagemResumo}`);
  }
  
  return result;
}

// Preview
async function getGamePreview(igdbId: number) {
  const response = await fetch(`/admin/igdb/preview/${igdbId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  if (response.ok) {
    return await response.json();
  }
  
  throw new Error('Jogo não encontrado');
}

// Busca com ordenação
async function searchGames(query: string, sortBy: string = 'rating') {
  const params = new URLSearchParams({
    nome: query,
    sortBy,
    sortDir: 'desc',
    limit: '20'
  });
  
  const response = await fetch(`/admin/igdb/search?${params}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
}
```

---

## Performance

### Importação em Lote
- Tempo médio: 1-2 segundos por jogo
- Recomendação: Máximo 20 jogos por lote
- Processamento sequencial para garantir consistência

### Preview
- Cache na IGDB: ~100-200ms
- Sem cache: ~500-1000ms
- Recomendação: Implementar cache no frontend

### Busca
- Tempo de resposta: 200-500ms
- Ordenação: Realizada no backend após busca na IGDB
- Paginação: Use `page` e `limit` para listas grandes
