# 🎮 Guia de Integração IGDB - Painel Admin

## 📋 Visão Geral

A aplicação possui uma **integração completa e automática** com a API IGDB (Internet Game Database) da Twitch. O admin pode buscar jogos, importá-los automaticamente com todos os dados (imagens, vídeos, descrições, ratings, etc) e apenas definir preço/estoque para ativá-los na loja.

---

## 🔐 Autenticação

Todos os endpoints abaixo requerem:
- **Role**: `ROLE_ADMIN`
- **Header**: `Authorization: Bearer {token}`

---

## 🎯 Fluxo Completo de Importação

### **Passo 1: Buscar Jogos na IGDB**

```http
GET /admin/igdb/search?nome=zelda&limit=10
```

**Parâmetros:**
- `nome` (string, obrigatório): Nome do jogo para buscar
- `limit` (int, opcional, padrão: 10): Quantidade de resultados

**Resposta:**
```json
[
  {
    "igdbId": 1234,
    "nome": "The Legend of Zelda: Breath of the Wild",
    "slug": "the-legend-of-zelda-breath-of-the-wild",
    "descricao": "Explore um vasto mundo aberto...",
    "dataLancamento": "2017-03-03",
    "rating": 9.7,
    "jaImportado": false,
    "produtoIdLocal": null
  }
]
```

**Frontend deve:**
- Exibir lista de jogos encontrados
- Mostrar badge "JÁ IMPORTADO" se `jaImportado: true`
- Permitir importar apenas jogos não importados

---

### **Passo 2: Importar Jogo Específico**

```http
POST /admin/igdb/import/{igdbId}
```

**Exemplo:**
```http
POST /admin/igdb/import/1234
```

**Resposta de Sucesso:**
```json
{
  "sucesso": true,
  "mensagem": "Jogo importado com sucesso",
  "produtoId": 42,
  "nomeProduto": "The Legend of Zelda: Breath of the Wild",
  "igdbId": 1234
}
```

**O que é importado automaticamente:**
- ✅ Nome, descrição curta e completa
- ✅ Slug (URL amigável)
- ✅ Data de lançamento
- ✅ Rating IGDB (0-10)
- ✅ Status (Released, Early Access, etc)
- ✅ Capa em alta resolução
- ✅ Screenshots (múltiplas imagens)
- ✅ Vídeos (trailers)
- ✅ Plataformas (PlayStation, Xbox, PC, etc)
- ✅ Gêneros (Action, RPG, etc)
- ✅ Links externos (Steam, Epic Games, site oficial)

**Valores padrão definidos:**
- `preco`: R$ 59,99
- `estoque`: 0
- `desconto`: 0
- `ativo`: **false** (produto não aparece na loja até admin ativar)
- `plataforma`: "Multiplataforma"
- `desenvolvedor`: "A definir"
- `publisher`: "A definir"

---

### **Passo 3: Definir Preço e Estoque**

```http
PATCH /produtos/{produtoId}/comercial
```

**Body:**
```json
{
  "preco": 199.90,
  "estoque": 50,
  "desconto": 10,
  "ativo": true
}
```

**Validações:**
- `preco`: Maior que 0
- `estoque`: Não pode ser negativo
- `desconto`: 0-100 (percentual)
- `ativo`: true/false

**Resposta:**
```json
{
  "id": 42,
  "nome": "The Legend of Zelda: Breath of the Wild",
  "preco": 199.90,
  "estoque": 50,
  "desconto": 10,
  "ativo": true,
  ...
}
```

**Agora o produto está visível na loja!**

---

## 🚀 Endpoints Adicionais

### **Importar Jogos Populares (Lote)**

```http
POST /admin/igdb/import/popular?quantidade=20
```

Importa os jogos mais populares da IGDB automaticamente.

**Resposta:**
```json
[
  {
    "sucesso": true,
    "produtoId": 43,
    "nomeProduto": "Elden Ring",
    "igdbId": 5678
  },
  ...
]
```

---

### **Sincronizar Produto**

Atualiza dados de um produto já importado com informações mais recentes da IGDB:

```http
PUT /admin/igdb/sync/{produtoId}
```

---

### **Estatísticas IGDB**

```http
GET /admin/igdb/stats
```

**Resposta:**
```json
{
  "totalProdutos": 150,
  "produtosIgdb": 120,
  "produtosAtivos": 80,
  "produtosDesatualizados": 5,
  "ultimaSincronizacao": "2025-12-13T10:30:00",
  "apiDisponivel": true,
  "statusApi": "Operacional"
}
```

---

### **Status da API IGDB**

```http
GET /admin/igdb/status
```

Verifica se a API da IGDB está acessível.

---

## 🖼️ Fluxo Frontend - UX Sugerida

### **Tela: Painel de Importação IGDB**

```
┌─────────────────────────────────────────────────────────────┐
│  🎮 Importar Jogos da IGDB                                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  🔍 [___________________________________________] [Buscar]   │
│                                                              │
│  📊 Estatísticas                                            │
│  • Total de produtos: 150                                   │
│  • Produtos IGDB: 120                                       │
│  • Produtos ativos: 80                                      │
│  • API Status: ✅ Operacional                              │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│  Resultados da Busca: "zelda"                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ 📷 [Capa]  The Legend of Zelda: Breath of the Wild    │ │
│  │            Rating: ⭐ 9.7  |  Lançamento: 03/03/2017  │ │
│  │            Explore um vasto mundo aberto...            │ │
│  │            [✅ Importar]                               │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ 📷 [Capa]  Zelda: Tears of the Kingdom                │ │
│  │            Rating: ⭐ 9.5  |  Lançamento: 12/05/2023  │ │
│  │            Continue a aventura...                      │ │
│  │            🔒 JÁ IMPORTADO - Produto #45              │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### **Tela: Produtos Importados (Aguardando Configuração)**

```
┌─────────────────────────────────────────────────────────────┐
│  ⚠️ Produtos Importados - Configuração Pendente             │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ 📷  Elden Ring                           🔴 INATIVO   │ │
│  │     Preço: R$ 59,99  |  Estoque: 0                    │ │
│  │                                                        │ │
│  │     Preço:   [______] R$                              │ │
│  │     Estoque: [______] unidades                        │ │
│  │     Desconto: [______] %                              │ │
│  │     [✅ Ativar na Loja]                               │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📱 Exemplo de Implementação Frontend (React)

```typescript
// services/igdbService.ts
export const igdbService = {
  // Buscar jogos
  async searchGames(nome: string, limit = 10) {
    const response = await api.get(`/admin/igdb/search`, {
      params: { nome, limit }
    });
    return response.data;
  },

  // Importar jogo específico
  async importGame(igdbId: number) {
    const response = await api.post(`/admin/igdb/import/${igdbId}`);
    return response.data;
  },

  // Atualizar dados comerciais
  async updateCommercialData(produtoId: number, data: {
    preco: number,
    estoque: number,
    desconto?: number,
    ativo: boolean
  }) {
    const response = await api.patch(`/produtos/${produtoId}/comercial`, data);
    return response.data;
  },

  // Obter estatísticas
  async getStats() {
    const response = await api.get('/admin/igdb/stats');
    return response.data;
  }
};

// Componente de busca
function IgdbSearchPanel() {
  const [searchTerm, setSearchTerm] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      const games = await igdbService.searchGames(searchTerm);
      setResults(games);
    } catch (error) {
      toast.error('Erro ao buscar jogos');
    } finally {
      setLoading(false);
    }
  };

  const handleImport = async (igdbId: number) => {
    try {
      const result = await igdbService.importGame(igdbId);
      toast.success(`Jogo importado! Configure preço e estoque.`);
      // Redirecionar para página de configuração
      navigate(`/admin/produtos/${result.produtoId}/editar`);
    } catch (error) {
      toast.error('Erro ao importar jogo');
    }
  };

  return (
    <div>
      <input 
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder="Buscar jogo na IGDB..."
      />
      <button onClick={handleSearch}>Buscar</button>

      {results.map(game => (
        <GameCard 
          key={game.igdbId}
          game={game}
          onImport={() => handleImport(game.igdbId)}
        />
      ))}
    </div>
  );
}
```

---

## ⚙️ Configuração Backend

As credenciais da IGDB/Twitch devem estar em `application.properties`:

```properties
# IGDB API Configuration
igdb.api.url=https://api.igdb.com/v4
igdb.client-id=${TWITCH_CLIENT_ID}
igdb.client-secret=${TWITCH_CLIENT_SECRET}
igdb.sync-interval-days=7
```

---

## 📝 Notas Importantes

1. **Produtos importados começam INATIVOS** - Admin deve revisar preço/estoque antes de ativar
2. **Preço padrão é R$ 59,99** - Apenas referência, admin deve ajustar
3. **Estoque padrão é 0** - Evita vendas acidentais antes de configuração
4. **Imagens já vêm em alta resolução** - Não é necessário upload manual
5. **Sincronização automática** - Produtos são atualizados a cada 7 dias (configurável)
6. **Rate limiting** - API IGDB tem limites, evite importações em lote muito grandes

---

## 🐛 Troubleshooting

### Erro: "API IGDB indisponível"
- Verificar credenciais Twitch no `application.properties`
- Testar endpoint `GET /admin/igdb/status`

### Erro: "Jogo já importado"
- Usar endpoint de sincronização ao invés de importar novamente
- Verificar `jaImportado: true` no resultado da busca

### Produto não aparece na loja após importação
- Verificar se `ativo: true`
- Verificar se `estoque > 0`
- Verificar se `preco > 0`

---

## 🎯 Checklist de Implementação Frontend

- [ ] Página de busca IGDB
- [ ] Exibir resultados com capa, rating, data
- [ ] Indicar jogos já importados
- [ ] Botão "Importar" funcional
- [ ] Página de produtos pendentes (inativos)
- [ ] Formulário de configuração comercial (preço/estoque)
- [ ] Dashboard com estatísticas IGDB
- [ ] Feedback visual de sucesso/erro

---

**✅ Com essa integração, o admin não perde tempo cadastrando dados manualmente!**
