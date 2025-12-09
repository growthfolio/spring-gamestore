# 📋 Requisitos de Frontend - Spring GameStore

## 🎯 Visão Geral

Desenvolvimento de interface web responsiva para e-commerce de jogos, integrando com API RESTful Spring Boot. Sistema com autenticação JWT, gestão de produtos/categorias, carrinho de compras, avaliações e favoritos.

---

## 🏗️ Estrutura Atual do Frontend

### ✅ **Componentes Existentes**
```
frontend/src/
├── pages/
│   └── home/                    # ✅ Página inicial com carousel
├── components/
│   ├── navbar/                  # ✅ Navegação principal
│   ├── footer/                  # ✅ Rodapé
│   ├── categorias/              # ✅ CRUD Categorias
│   │   ├── listaCategorias/
│   │   ├── cardCategorias/
│   │   ├── formularioCategoria/
│   │   └── deletarCategorias/
│   └── produtos/                # ✅ CRUD Produtos
│       ├── listaProdutos/
│       ├── cardProdutos/
│       ├── formularioProduto/
│       └── deletarProdutos/
```

### ❌ **Telas Faltantes (PRIORIDADE)**
1. **Autenticação**
   - Login
   - Cadastro de usuário
   - Perfil do usuário

2. **Catálogo de Produtos**
   - Página de detalhes do produto
   - Busca e filtros avançados
   - Paginação

3. **Carrinho de Compras**
   - Visualização do carrinho
   - Adicionar/remover itens
   - Cálculo de subtotais

4. **Favoritos**
   - Lista de produtos favoritos
   - Toggle favoritar/desfavoritar

5. **Avaliações**
   - Sistema de avaliação (1-5 estrelas)
   - Listagem de avaliações por produto
   - Formulário de avaliação

6. **Admin IGDB** (⚠️ Apenas ADMIN)
   - Painel de importação IGDB
   - Busca de jogos na API
   - Sincronização de produtos
   - Estatísticas

---

## 📊 Endpoints da API Disponíveis

### 🔐 **Autenticação (/usuarios)**

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/usuarios/cadastrar` | Cadastrar novo usuário | ❌ Público |
| POST | `/usuarios/logar` | Login (retorna JWT) | ❌ Público |
| GET | `/usuarios/{id}` | Buscar usuário por ID | ✅ Requerida |
| GET | `/usuarios/all` | Listar todos usuários | ✅ Requerida |
| PUT | `/usuarios/atualizar/{id}` | Atualizar usuário | ✅ Requerida |

**Request/Response:**
```typescript
// POST /usuarios/cadastrar
interface CadastroRequest {
  nome: string;
  usuario: string;
  senha: string;
  foto?: string;
}

// POST /usuarios/logar
interface LoginRequest {
  usuario: string;
  senha: string;
}

interface LoginResponse {
  id: number;
  nome: string;
  usuario: string;
  foto?: string;
  token: string;
  tipo: 'USER' | 'ADMIN';
}
```

---

### 📦 **Produtos (/produtos)**

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/produtos` | Listar produtos (paginado) | ❌ Público |
| GET | `/produtos/{id}` | Detalhes do produto | ❌ Público |
| GET | `/produtos/buscar?nome=X` | Buscar por nome | ❌ Público |
| POST | `/produtos` | Criar produto | ✅ ADMIN |
| PUT | `/produtos/{id}` | Atualizar produto | ✅ ADMIN |
| DELETE | `/produtos/{id}` | Deletar produto | ✅ ADMIN |

**Parâmetros de Paginação:**
```typescript
interface PaginationParams {
  page?: number;        // Página atual (default: 0)
  size?: number;        // Itens por página (default: 10)
  sort?: string;        // Campo de ordenação (ex: "nome,asc")
}

// GET /produtos?page=0&size=12&sort=nome,asc
```

**Response:**
```typescript
interface ProdutoResponse {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
  quantidade: number;
  foto?: string;
  disponivel: boolean;
  categoria: {
    id: number;
    tipo: string;
  };
  generos?: Array<{ id: number; tipo: string }>;
  plataformas?: string[];
  avaliacaoMedia?: number;
  totalAvaliacoes?: number;
}

interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;       // Página atual
  size: number;         // Itens por página
  first: boolean;
  last: boolean;
}
```

---

### 🏷️ **Categorias (/categorias)**

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/categorias` | Listar categorias | ❌ Público |
| GET | `/categorias/{id}` | Detalhes da categoria | ❌ Público |
| POST | `/categorias` | Criar categoria | ✅ ADMIN |
| PUT | `/categorias/{id}` | Atualizar categoria | ✅ ADMIN |
| DELETE | `/categorias/{id}` | Deletar categoria | ✅ ADMIN |

**Request/Response:**
```typescript
interface CategoriaRequest {
  tipo: string;
  descricao?: string;
}

interface CategoriaResponse {
  id: number;
  tipo: string;
  descricao?: string;
  produtos?: ProdutoResponse[];
}
```

---

### 🛒 **Carrinho (/carrinho)**

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/carrinho` | Adicionar item | ✅ Requerida |
| GET | `/carrinho` | Ver carrinho | ✅ Requerida |
| GET | `/carrinho/contagem` | Total de itens | ✅ Requerida |
| DELETE | `/carrinho/produto/{id}` | Remover item | ✅ Requerida |
| DELETE | `/carrinho` | Limpar carrinho | ✅ Requerida |

**Request/Response:**
```typescript
interface AdicionarCarrinhoRequest {
  produtoId: number;
  quantidade: number;
}

interface CarrinhoItemResponse {
  id: number;
  produto: ProdutoResponse;
  quantidade: number;
  subtotal: number;
}

interface CarrinhoResponse {
  itens: CarrinhoItemResponse[];
  total: number;
  totalItens: number;
}
```

---

### ⭐ **Avaliações (/avaliacoes)**

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/avaliacoes` | Criar avaliação | ✅ Requerida |
| GET | `/avaliacoes/{id}` | Detalhes avaliação | ❌ Público |
| GET | `/avaliacoes/produto/{id}` | Avaliações do produto | ❌ Público |
| GET | `/avaliacoes/produto/{id}/media` | Média de avaliações | ❌ Público |
| GET | `/avaliacoes/produto/{id}/contagem` | Total de avaliações | ❌ Público |
| PUT | `/avaliacoes/{id}` | Atualizar avaliação | ✅ Requerida |
| DELETE | `/avaliacoes/{id}` | Deletar avaliação | ✅ Requerida |

**Request/Response:**
```typescript
interface AvaliacaoRequest {
  produtoId: number;
  nota: number;         // 1 a 5
  comentario?: string;
}

interface AvaliacaoResponse {
  id: number;
  nota: number;
  comentario?: string;
  usuario: {
    id: number;
    nome: string;
    foto?: string;
  };
  produto: {
    id: number;
    nome: string;
  };
  dataAvaliacao: string;  // ISO 8601
}

interface MediaAvaliacaoResponse {
  media: number;
  total: number;
  distribuicao: {
    estrela5: number;
    estrela4: number;
    estrela3: number;
    estrela2: number;
    estrela1: number;
  };
}
```

---

### ❤️ **Favoritos (/favoritos)**

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/favoritos/produto/{id}` | Adicionar favorito | ✅ Requerida |
| DELETE | `/favoritos/produto/{id}` | Remover favorito | ✅ Requerida |
| GET | `/favoritos` | Listar favoritos | ✅ Requerida |
| GET | `/favoritos/produto/{id}/verificar` | Verificar se é favorito | ✅ Requerida |
| GET | `/favoritos/contagem` | Total de favoritos | ✅ Requerida |

**Response:**
```typescript
interface FavoritoResponse {
  id: number;
  produto: ProdutoResponse;
  dataFavorito: string;
}
```

---

### 🎮 **Admin IGDB (/admin/igdb)** ⚠️ Apenas ADMIN

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/admin/igdb/search?gameName=X&limit=10` | Buscar jogos na IGDB |
| POST | `/admin/igdb/import/{igdbId}` | Importar jogo por ID |
| POST | `/admin/igdb/import/popular?quantity=20` | Importar populares |
| GET | `/admin/igdb/status` | Status da integração |
| GET | `/admin/igdb/stats` | Estatísticas IGDB |
| PUT | `/admin/igdb/sync/{produtoId}` | Sincronizar produto |
| POST | `/admin/igdb/sync/all` | Sincronizar todos |
| POST | `/admin/igdb/sync/manual` | Sincronização manual |
| PUT | `/admin/igdb/sync/toggle/{produtoId}` | Ativar/desativar sync |

**Response:**
```typescript
interface IgdbGameDTO {
  id: number;
  name: string;
  summary?: string;
  cover?: string;
  rating?: number;
  releaseDate?: string;
}

interface IgdbStatsResponse {
  totalProdutosIgdb: number;
  produtosComSyncAtivo: number;
  ultimaSincronizacao?: string;
  produtosDesatualizados: number;
}
```

---

## 🎨 Requisitos de Telas

### **1. Página de Login (/login)**

**Funcionalidades:**
- Formulário email/senha
- Botão "Entrar"
- Link para cadastro
- Validação de campos
- Mensagens de erro (credenciais inválidas)
- Redirecionamento após login (Home ou Admin)

**API:**
```typescript
POST /usuarios/logar
Body: { usuario: string, senha: string }
Response: { token: string, tipo: 'USER' | 'ADMIN', ... }
```

**Estados:**
- Loading durante autenticação
- Erro de credenciais
- Sucesso (armazenar token no localStorage/Context)

---

### **2. Página de Cadastro (/cadastro)**

**Funcionalidades:**
- Formulário: nome, email, senha, confirmar senha
- Upload de foto (opcional)
- Validações:
  - Email único
  - Senha >= 8 caracteres
  - Senhas coincidem
- Botão "Cadastrar"
- Link para login

**API:**
```typescript
POST /usuarios/cadastrar
Body: { nome: string, usuario: string, senha: string, foto?: string }
```

---

### **3. Catálogo de Produtos (/produtos)**

**Funcionalidades:**
- Grid responsivo de cards (3-4 colunas)
- Cada card:
  - Imagem do produto
  - Nome
  - Preço (R$ formatado)
  - Avaliação média (estrelas)
  - Botão "Ver detalhes"
  - Ícone favorito (coração)
  - Botão "Adicionar ao carrinho"
- Filtros:
  - Por categoria (dropdown/sidebar)
  - Por preço (range slider)
  - Por nome (busca)
  - Por disponibilidade
- Ordenação:
  - Mais recente
  - Menor preço
  - Maior preço
  - Melhor avaliação
- Paginação (10-12 itens por página)

**API:**
```typescript
GET /produtos?page=0&size=12&sort=nome,asc
GET /produtos/buscar?nome=zelda
GET /categorias (para filtros)
```

---

### **4. Detalhes do Produto (/produtos/:id)**

**Funcionalidades:**
- Galeria de imagens (principal + thumbnails)
- Informações:
  - Nome
  - Descrição completa
  - Preço (destaque)
  - Categoria/Gêneros
  - Plataformas
  - Disponibilidade
- Avaliação:
  - Média (estrelas grandes)
  - Total de avaliações
  - Distribuição (5⭐: X, 4⭐: Y, etc.)
- Ações:
  - Quantidade (input numérico)
  - "Adicionar ao carrinho"
  - "Favoritar" (toggle)
- Seção de avaliações:
  - Lista de comentários
  - Ordenação (mais recente, maior nota)
  - Formulário para avaliar (se logado)
- Produtos relacionados (mesma categoria)

**APIs:**
```typescript
GET /produtos/{id}
GET /avaliacoes/produto/{id}
GET /avaliacoes/produto/{id}/media
POST /carrinho (adicionar)
POST /favoritos/produto/{id} (favoritar)
POST /avaliacoes (avaliar)
```

---

### **5. Carrinho de Compras (/carrinho)**

**Funcionalidades:**
- Lista de itens:
  - Imagem miniatura
  - Nome do produto
  - Preço unitário
  - Quantidade (editar inline)
  - Subtotal
  - Botão remover (X)
- Resumo do pedido:
  - Subtotal
  - Descontos (se houver)
  - Total
- Botões:
  - "Continuar comprando"
  - "Limpar carrinho"
  - "Finalizar compra" (placeholder por enquanto)
- Carrinho vazio (mensagem + link para produtos)
- Badge no ícone do carrinho (header) com quantidade

**APIs:**
```typescript
GET /carrinho
POST /carrinho (adicionar item)
DELETE /carrinho/produto/{id} (remover)
DELETE /carrinho (limpar)
GET /carrinho/contagem (badge)
```

---

### **6. Meus Favoritos (/favoritos)**

**Funcionalidades:**
- Grid de produtos favoritos
- Cada card:
  - Mesma estrutura do catálogo
  - Botão "Remover dos favoritos"
  - Link para detalhes
- Mensagem se lista vazia
- Badge no ícone de favoritos (header) com contagem

**APIs:**
```typescript
GET /favoritos
DELETE /favoritos/produto/{id}
GET /favoritos/contagem (badge)
```

---

### **7. Perfil do Usuário (/perfil)**

**Funcionalidades:**
- Informações do usuário:
  - Foto
  - Nome
  - Email
- Formulário de edição:
  - Editar nome
  - Upload nova foto
  - Alterar senha
- Estatísticas:
  - Total de favoritos
  - Total de avaliações feitas
  - Produtos no carrinho
- Botão "Sair" (logout)

**APIs:**
```typescript
GET /usuarios/{id}
PUT /usuarios/atualizar/{id}
GET /favoritos/contagem
GET /carrinho/contagem
```

---

### **8. Painel Admin IGDB (/admin/igdb)** ⚠️ Apenas ADMIN

**Funcionalidades:**

#### **8.1 Dashboard**
- Cards com estatísticas:
  - Total produtos da IGDB
  - Produtos com sync ativo
  - Produtos desatualizados
  - Última sincronização
- Botões rápidos:
  - "Importar populares"
  - "Sincronizar todos"
  - "Sincronização manual"

#### **8.2 Buscar Jogos**
- Input de busca
- Botão "Buscar"
- Lista de resultados:
  - Nome do jogo
  - Capa
  - Rating IGDB
  - Data de lançamento
  - Botão "Importar"
- Feedback de importação (sucesso/erro)

#### **8.3 Produtos Importados**
- Tabela com produtos da IGDB:
  - Nome
  - ID IGDB
  - Sync ativo (toggle)
  - Última sincronização
  - Ações:
    - "Sincronizar agora"
    - "Desabilitar sync"
    - "Ver detalhes"

#### **8.4 Importar Populares**
- Modal/página:
  - Input quantidade (default: 20)
  - Botão "Importar"
  - Loading com progresso
  - Lista de jogos importados

**APIs:**
```typescript
GET /admin/igdb/stats
GET /admin/igdb/status
GET /admin/igdb/search?gameName=zelda&limit=10
POST /admin/igdb/import/{igdbId}
POST /admin/igdb/import/popular?quantity=20
PUT /admin/igdb/sync/{produtoId}
POST /admin/igdb/sync/all
PUT /admin/igdb/sync/toggle/{produtoId}
```

---

## 🔐 Gerenciamento de Autenticação

### **Context/Store de Autenticação**

```typescript
interface AuthState {
  isAuthenticated: boolean;
  user: {
    id: number;
    nome: string;
    usuario: string;
    foto?: string;
    tipo: 'USER' | 'ADMIN';
  } | null;
  token: string | null;
}

interface AuthActions {
  login(credentials: LoginRequest): Promise<void>;
  cadastrar(data: CadastroRequest): Promise<void>;
  logout(): void;
  atualizarUsuario(data: UpdateUserRequest): Promise<void>;
}
```

### **Interceptor Axios**

```typescript
// services/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

// Adicionar token em todas requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Tratar erros 401 (redirecionar para login)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 🎨 Componentes Reutilizáveis Necessários

### **1. ProductCard**
- Props: produto, onAddToCart, onToggleFavorite
- Estados: loading, isFavorite
- Layout responsivo

### **2. StarRating**
- Props: rating (0-5), size, readonly, onChange
- Exibir estrelas preenchidas/vazias

### **3. QuantityInput**
- Props: value, onChange, min, max
- Botões +/- e input numérico

### **4. PriceDisplay**
- Props: value
- Formatação: R$ 199,90

### **5. Pagination**
- Props: currentPage, totalPages, onPageChange
- Botões anterior/próximo + números

### **6. FilterSidebar**
- Props: categorias, filters, onFilterChange
- Checkboxes, range sliders

### **7. Loading/Spinner**
- Indicador de carregamento

### **8. ErrorBoundary**
- Capturar erros React

### **9. Toast/Notification**
- Feedback de ações (sucesso/erro)

### **10. ProtectedRoute**
- Redirecionar para login se não autenticado
- Verificar role ADMIN

---

## 🚀 Priorização de Desenvolvimento

### **FASE 1 - Funcionalidades Críticas** (Semana 1-2)
1. ✅ Navbar/Footer (já existem)
2. ✅ Home com carousel (já existe)
3. ⬜ **Autenticação (Login/Cadastro)**
4. ⬜ **Catálogo de Produtos (com paginação)**
5. ⬜ **Detalhes do Produto**
6. ⬜ **Carrinho de Compras**

### **FASE 2 - Funcionalidades Sociais** (Semana 3)
7. ⬜ Sistema de Favoritos
8. ⬜ Sistema de Avaliações
9. ⬜ Perfil do Usuário
10. ⬜ Busca e Filtros Avançados

### **FASE 3 - Admin** (Semana 4)
11. ⬜ Painel Admin IGDB
12. ⬜ Importação de Jogos
13. ⬜ Gerenciamento de Sincronização

### **FASE 4 - Polimento** (Semana 5)
14. ⬜ Testes E2E
15. ⬜ Responsividade mobile
16. ⬜ Performance (lazy loading, memoization)
17. ⬜ SEO/Meta tags
18. ⬜ Acessibilidade

---

## 📦 Dependências Sugeridas

```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "axios": "^1.6.2",
    "react-hook-form": "^7.48.2",
    "zod": "^3.22.4",
    "@hookform/resolvers": "^3.3.2",
    "react-hot-toast": "^2.4.1",
    "@tanstack/react-query": "^5.12.2",
    "date-fns": "^2.30.0",
    "clsx": "^2.0.0",
    "lucide-react": "^0.294.0"
  }
}
```

---

## 🎯 Critérios de Aceitação

### **Funcionalidades Obrigatórias:**
- ✅ Autenticação JWT funcional
- ✅ Proteção de rotas (público/autenticado/admin)
- ✅ CRUD completo de produtos
- ✅ Carrinho persistente (localStorage)
- ✅ Paginação e busca funcionais
- ✅ Responsividade mobile (< 768px)
- ✅ Tratamento de erros com feedback visual

### **Performance:**
- ✅ Lighthouse Score > 80
- ✅ Lazy loading de imagens
- ✅ Code splitting por rota

### **UX:**
- ✅ Loading states em todas requisições
- ✅ Mensagens de sucesso/erro (toast)
- ✅ Confirmação antes de deletar
- ✅ Validação de formulários
- ✅ Acessibilidade (ARIA labels, navegação por teclado)

---

**🎮 Pronto para começar o desenvolvimento!** Qual fase você quer priorizar?
