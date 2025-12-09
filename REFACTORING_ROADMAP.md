# 🎯 ROADMAP DE REFATORAÇÃO
**Branch:** `feature/professional-refactoring`  
**Objetivo:** Transformar o código de nível acadêmico para nível de portfólio

---

## 📊 PROGRESSO GERAL

- [ ] **FASE 1** - Fundação Crítica (0/5)
- [ ] **FASE 2** - Qualidade e Segurança (0/5)
- [ ] **FASE 3** - Features de Negócio (0/4)
- [ ] **FASE 4** - Pré-Produção (0/4)

**Status Atual:** 🟡 Não iniciado  
**Última Atualização:** 2025-12-09

---

## ✅ FASE 1 - FUNDAÇÃO CRÍTICA

### 1.1 Implementar DTOs Completos
**Status:** ⬜ Não iniciado  
**Prioridade:** 🔴 CRÍTICA

**Tarefas:**
- [ ] Criar package `dto` com subpackages:
  - [ ] `dto/request/` - DTOs de entrada
  - [ ] `dto/response/` - DTOs de saída
  - [ ] `dto/mapper/` - Mappers de conversão
- [ ] Criar DTOs para Categoria:
  - [ ] `CategoriaRequestDTO`
  - [ ] `CategoriaResponseDTO`
  - [ ] `CategoriaMapper`
- [ ] Criar DTOs para Produto:
  - [ ] `ProdutoRequestDTO`
  - [ ] `ProdutoResponseDTO`
  - [ ] `ProdutoMapper`
- [ ] Criar DTOs para Usuario:
  - [ ] `UsuarioRequestDTO` (cadastro)
  - [ ] `UsuarioUpdateRequestDTO` (atualização)
  - [ ] `UsuarioResponseDTO`
  - [ ] `UsuarioMapper`
- [ ] Mover `UsuarioLogin` para package `dto/request/`
- [ ] Criar `AuthResponseDTO` (resposta de login com token)

**Arquivos a Criar:**
```
src/main/java/com/energygames/lojadegames/dto/
├── request/
│   ├── CategoriaRequestDTO.java
│   ├── ProdutoRequestDTO.java
│   ├── UsuarioRequestDTO.java
│   ├── UsuarioUpdateRequestDTO.java
│   └── LoginRequestDTO.java (ex-UsuarioLogin)
├── response/
│   ├── CategoriaResponseDTO.java
│   ├── ProdutoResponseDTO.java
│   ├── UsuarioResponseDTO.java
│   └── AuthResponseDTO.java
└── mapper/
    ├── CategoriaMapper.java
    ├── ProdutoMapper.java
    └── UsuarioMapper.java
```

---

### 1.2 Exception Handler Global
**Status:** ⬜ Não iniciado  
**Prioridade:** 🔴 CRÍTICA

**Tarefas:**
- [ ] Criar package `exception`
- [ ] Criar exceções customizadas:
  - [ ] `ResourceNotFoundException`
  - [ ] `BusinessException`
  - [ ] `UnauthorizedException`
  - [ ] `DuplicateResourceException`
- [ ] Criar DTOs de erro:
  - [ ] `ErrorResponse`
  - [ ] `ValidationErrorResponse`
- [ ] Criar `GlobalExceptionHandler` com `@RestControllerAdvice`
- [ ] Implementar handlers para:
  - [ ] `ResourceNotFoundException` → 404
  - [ ] `BusinessException` → 400
  - [ ] `UnauthorizedException` → 401
  - [ ] `AccessDeniedException` → 403
  - [ ] `MethodArgumentNotValidException` → 400 (validação)
  - [ ] `Exception` → 500 (genérico)
- [ ] Adicionar logs estruturados em cada handler

**Arquivos a Criar:**
```
src/main/java/com/energygames/lojadegames/exception/
├── ResourceNotFoundException.java
├── BusinessException.java
├── UnauthorizedException.java
├── DuplicateResourceException.java
├── ErrorResponse.java
├── ValidationErrorResponse.java
└── GlobalExceptionHandler.java
```

---

### 1.3 Completar Camada Service
**Status:** ⬜ Não iniciado  
**Prioridade:** 🔴 CRÍTICA

**Tarefas:**
- [ ] Criar `CategoriaService` e `CategoriaServiceImpl`
  - [ ] Mover lógica do `CategoriaController` para service
  - [ ] Adicionar validações de negócio
  - [ ] Validar se categoria está ativa antes de associar produtos
  - [ ] Implementar soft delete (marcar como inativo)
- [ ] Criar `ProdutoService` e `ProdutoServiceImpl`
  - [ ] Mover lógica do `ProdutoController` para service
  - [ ] Validar existência de categoria
  - [ ] Validar estoque antes de operações
  - [ ] Calcular preço com desconto
  - [ ] Implementar soft delete
- [ ] Refatorar `UsuarioService`
  - [ ] Separar interface e implementação
  - [ ] Adicionar método `obterUsuarioAutenticado()`
  - [ ] Melhorar validações
- [ ] Atualizar todos os controllers para usar services
- [ ] Remover acesso direto a repositories nos controllers

**Arquivos a Criar/Modificar:**
```
src/main/java/com/energygames/lojadegames/service/
├── CategoriaService.java (interface)
├── ProdutoService.java (interface)
├── UsuarioService.java (interface - extrair)
└── impl/
    ├── CategoriaServiceImpl.java
    ├── ProdutoServiceImpl.java
    └── UsuarioServiceImpl.java (renomear atual)
```

---

### 1.4 Externalize JWT Secret
**Status:** ⬜ Não iniciado  
**Prioridade:** 🔴 CRÍTICA (Segurança)

**Tarefas:**
- [ ] Remover secret hardcoded de `JwtService`
- [ ] Adicionar propriedades no `application.properties`:
  - [ ] `jwt.secret=${JWT_SECRET:default-dev-secret}`
  - [ ] `jwt.expiration=${JWT_EXPIRATION:3600000}`
- [ ] Injetar valores com `@Value` no `JwtService`
- [ ] Criar `.env.example` com variáveis necessárias
- [ ] Atualizar `.gitignore` para ignorar `.env`
- [ ] Adicionar validação: secret deve ter mínimo de 256 bits

**Arquivos a Modificar:**
```
src/main/java/com/energygames/lojadegames/security/JwtService.java
src/main/resources/application.properties
.env.example (criar)
.gitignore (atualizar)
```

---

### 1.5 Sistema de Roles
**Status:** ⬜ Não iniciado  
**Prioridade:** 🔴 CRÍTICA (Segurança)

**Tarefas:**
- [ ] Criar `RoleEnum` (ROLE_USER, ROLE_ADMIN)
- [ ] Adicionar campo `roles` em `Usuario`:
  - [ ] `@ElementCollection` com `Set<RoleEnum>`
  - [ ] Inicializar com ROLE_USER por padrão
- [ ] Atualizar `UserDetailsImpl`:
  - [ ] Implementar `getAuthorities()` corretamente
  - [ ] Converter roles para `GrantedAuthority`
- [ ] Atualizar `BasicSecurityConfig`:
  - [ ] Proteger endpoints de admin com `@PreAuthorize("hasRole('ADMIN')")`
  - [ ] POST/PUT/DELETE de categorias → ADMIN
  - [ ] POST/PUT/DELETE de produtos → ADMIN
  - [ ] GET público
- [ ] Adicionar `@EnableMethodSecurity` na configuração
- [ ] Atualizar cadastro: permitir criar admin via flag opcional

**Arquivos a Criar/Modificar:**
```
src/main/java/com/energygames/lojadegames/enums/
└── RoleEnum.java (criar)

Modificar:
- model/Usuario.java
- security/UserDetailsImpl.java
- security/BasicSecurityConfig.java
- service/UsuarioService.java
```

---

## ✅ FASE 2 - QUALIDADE E SEGURANÇA

### 2.1 Corrigir Double → BigDecimal
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [ ] Alterar tipo de `preco` em `Produto`: `Double` → `BigDecimal`
- [ ] Alterar tipo de `desconto` em `Produto`: `Double` → `BigDecimal`
- [ ] Adicionar validações:
  - [ ] `@DecimalMin("0.01")` para preço
  - [ ] `@DecimalMin("0")` e `@DecimalMax("100")` para desconto
  - [ ] `@Digits(integer=8, fraction=2)` para preço
- [ ] Atualizar cálculos de preço com desconto
- [ ] Atualizar todos os DTOs relacionados
- [ ] Usar `RoundingMode.HALF_UP` em divisões

**Impacto:** Schema do banco será alterado (campo de tipo DECIMAL)

---

### 2.2 Implementar Paginação
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [ ] Atualizar método `getAll()` de `CategoriaController`:
  - [ ] Adicionar parâmetros: `page`, `size`, `sort`
  - [ ] Retornar `Page<CategoriaResponseDTO>`
- [ ] Atualizar método `getAll()` de `ProdutoController`:
  - [ ] Adicionar parâmetros: `page`, `size`, `sort`
  - [ ] Retornar `Page<ProdutoResponseDTO>`
  - [ ] Adicionar filtros: `nome`, `categoriaId`, `plataforma`
- [ ] Criar `ProdutoSpecification` para queries dinâmicas
- [ ] Atualizar repositories para usar `JpaSpecificationExecutor`
- [ ] Configurar tamanho padrão de página: 20 itens
- [ ] Adicionar metadata de paginação nas respostas

**Arquivos a Criar/Modificar:**
```
src/main/java/com/energygames/lojadegames/repository/specification/
└── ProdutoSpecification.java (criar)

Modificar:
- repository/ProdutoRepository.java
- service/ProdutoService.java
- controller/ProdutoController.java
- controller/CategoriaController.java
```

---

### 2.3 Logging Estruturado
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [ ] Adicionar SLF4J/Logback (já vem com Spring Boot)
- [ ] Adicionar logs em todos os services:
  - [ ] `log.info()` para operações importantes
  - [ ] `log.warn()` para validações falhadas
  - [ ] `log.error()` para exceções
- [ ] Adicionar logs de segurança:
  - [ ] Login bem-sucedido
  - [ ] Login falhado
  - [ ] Acesso negado
- [ ] Criar `logback-spring.xml` customizado:
  - [ ] Pattern com timestamp, level, classe, mensagem
  - [ ] Arquivo rotativo de logs
  - [ ] Diferentes níveis por ambiente (dev/prod)
- [ ] Adicionar MDC para rastreamento de requisições

**Arquivos a Criar:**
```
src/main/resources/
└── logback-spring.xml
```

---

### 2.4 Validações de Negócio
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [ ] Categoria:
  - [ ] Não permitir deletar categoria com produtos ativos
  - [ ] Não permitir criar categoria com nome duplicado
  - [ ] Validar se está ativa antes de associar produtos
- [ ] Produto:
  - [ ] Validar se categoria existe e está ativa
  - [ ] Validar se desconto é coerente com preço
  - [ ] Não permitir estoque negativo
  - [ ] Validar data de lançamento (não futura demais)
  - [ ] Validar URLs de imagens
- [ ] Usuario:
  - [ ] Email único
  - [ ] Senha forte (regex)
  - [ ] Não permitir alterar email para um já existente

---

### 2.5 Testes Unitários Básicos
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [ ] Configurar JUnit 5 + Mockito
- [ ] Testes para `ProdutoService`:
  - [ ] `deveCriarProduto()`
  - [ ] `deveLancarExcecaoQuandoCategoriaNaoExiste()`
  - [ ] `deveCalcularPrecoComDescontoCorretamente()`
- [ ] Testes para `CategoriaService`:
  - [ ] `deveCriarCategoria()`
  - [ ] `deveLancarExcecaoQuandoNomeDuplicado()`
- [ ] Testes para `UsuarioService`:
  - [ ] `deveCadastrarUsuario()`
  - [ ] `deveLancarExcecaoQuandoEmailDuplicado()`
  - [ ] `deveAutenticarUsuarioComSucesso()`
- [ ] Testes de integração básicos para controllers
- [ ] Configurar cobertura mínima: 60%

**Arquivos a Criar:**
```
src/test/java/com/energygames/lojadegames/service/
├── ProdutoServiceTest.java
├── CategoriaServiceTest.java
└── UsuarioServiceTest.java
```

---

## ✅ FASE 3 - FEATURES DE NEGÓCIO

### 3.1 Sistema de Avaliações
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟡 MÉDIA

**Tarefas:**
- [ ] Criar entidade `Avaliacao`:
  - [ ] Relacionamento `@ManyToOne` com Usuario
  - [ ] Relacionamento `@ManyToOne` com Produto
  - [ ] Nota (1-5 estrelas)
  - [ ] Comentário (opcional)
  - [ ] Data da avaliação
  - [ ] Likes/Dislikes
- [ ] Criar `AvaliacaoRepository`
- [ ] Criar DTOs: `AvaliacaoRequestDTO`, `AvaliacaoResponseDTO`
- [ ] Criar `AvaliacaoService`:
  - [ ] Validar se usuário já avaliou o produto
  - [ ] Calcular média de avaliações do produto
  - [ ] Atualizar própria avaliação
  - [ ] Deletar própria avaliação
- [ ] Criar `AvaliacaoController`:
  - [ ] GET `/api/produtos/{id}/avaliacoes` (público, paginado)
  - [ ] POST `/api/produtos/{id}/avaliacoes` (autenticado)
  - [ ] PUT `/api/avaliacoes/{id}` (próprio usuário)
  - [ ] DELETE `/api/avaliacoes/{id}` (próprio usuário ou admin)
- [ ] Adicionar campo `mediaAvaliacoes` calculado em `Produto`

**Entidade Nova:**
```java
@Entity
public class Avaliacao {
    Long id;
    Usuario usuario;
    Produto produto;
    Integer nota; // 1-5
    String comentario;
    LocalDateTime dataAvaliacao;
    Integer likes;
    Integer dislikes;
}
```

---

### 3.2 Sistema de Favoritos
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟡 MÉDIA

**Tarefas:**
- [ ] Adicionar relacionamento `@ManyToMany` em `Usuario`:
  - [ ] `Set<Produto> favoritos`
  - [ ] Tabela join: `usuario_favoritos`
- [ ] Criar endpoints em `UsuarioController`:
  - [ ] GET `/api/usuarios/me/favoritos` (listar favoritos)
  - [ ] POST `/api/usuarios/me/favoritos/{produtoId}` (adicionar)
  - [ ] DELETE `/api/usuarios/me/favoritos/{produtoId}` (remover)
  - [ ] GET `/api/usuarios/me/favoritos/{produtoId}` (verificar)
- [ ] Adicionar métodos em `UsuarioService`:
  - [ ] `adicionarFavorito(Long produtoId)`
  - [ ] `removerFavorito(Long produtoId)`
  - [ ] `listarFavoritos(Pageable)`
- [ ] Validar se produto existe antes de favoritar
- [ ] Impedir duplicação de favoritos

---

### 3.3 Carrinho de Compras
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟡 MÉDIA

**Tarefas:**
- [ ] Criar entidade `Carrinho`:
  - [ ] Relacionamento `@OneToOne` com Usuario
  - [ ] `@OneToMany` com `ItemCarrinho`
- [ ] Criar entidade `ItemCarrinho`:
  - [ ] Relacionamento `@ManyToOne` com Carrinho
  - [ ] Relacionamento `@ManyToOne` com Produto
  - [ ] Quantidade
  - [ ] Preço unitário (snapshot)
- [ ] Criar repositories: `CarrinhoRepository`, `ItemCarrinhoRepository`
- [ ] Criar DTOs completos
- [ ] Criar `CarrinhoService`:
  - [ ] Obter carrinho atual
  - [ ] Adicionar item (validar estoque)
  - [ ] Atualizar quantidade
  - [ ] Remover item
  - [ ] Limpar carrinho
  - [ ] Calcular total
- [ ] Criar `CarrinhoController`:
  - [ ] GET `/api/carrinho` (ver carrinho)
  - [ ] POST `/api/carrinho/itens` (adicionar)
  - [ ] PUT `/api/carrinho/itens/{id}` (atualizar)
  - [ ] DELETE `/api/carrinho/itens/{id}` (remover)
  - [ ] DELETE `/api/carrinho` (limpar)
- [ ] Criar carrinho automaticamente no cadastro do usuário

**Entidades Novas:**
```java
@Entity
public class Carrinho {
    Long id;
    Usuario usuario;
    List<ItemCarrinho> itens;
}

@Entity
public class ItemCarrinho {
    Long id;
    Carrinho carrinho;
    Produto produto;
    Integer quantidade;
    BigDecimal precoUnitario;
}
```

---

### 3.4 Integração API Externa (RAWG)
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟡 BAIXA

**Tarefas:**
- [ ] Criar conta na RAWG API (https://rawg.io/apidocs)
- [ ] Adicionar dependências:
  - [ ] Spring WebClient ou RestTemplate
- [ ] Criar `RawgApiService`:
  - [ ] Buscar jogos por nome
  - [ ] Obter detalhes de jogo (imagens, descrição, plataformas)
  - [ ] Tratamento de rate limit
- [ ] Criar endpoint auxiliar:
  - [ ] GET `/api/games/search?query=zelda` (buscar na API)
  - [ ] Endpoint para admin importar dados
- [ ] Adicionar cache para reduzir chamadas à API
- [ ] Configurar timeout e retry

**Dependências a Adicionar:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

---

## ✅ FASE 4 - PRÉ-PRODUÇÃO

### 4.1 Migrations com Flyway
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟢 PRÉ-DEPLOY

**Tarefas:**
- [ ] Adicionar dependência Flyway
- [ ] Gerar SQL do schema atual:
  - [ ] `V1__create_initial_schema.sql`
- [ ] Configurar Flyway:
  - [ ] `spring.flyway.enabled=true` (apenas prod)
  - [ ] `spring.flyway.baseline-on-migrate=true`
- [ ] Alterar `ddl-auto`:
  - [ ] Dev: `validate` (Flyway controla)
  - [ ] Prod: `validate`
- [ ] Criar migrations para alterações futuras

**⚠️ Executar apenas quando schema estiver estável!**

---

### 4.2 Docker Compose Completo
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟢 PRÉ-DEPLOY

**Tarefas:**
- [ ] Criar `docker-compose.yml`:
  - [ ] Service MySQL
  - [ ] Service App (Spring Boot)
  - [ ] Volumes persistentes
  - [ ] Networks isoladas
- [ ] Criar `Dockerfile` otimizado:
  - [ ] Multi-stage build
  - [ ] Imagem JRE Alpine (menor)
- [ ] Criar scripts auxiliares:
  - [ ] `docker-build.sh`
  - [ ] `docker-run.sh`
  - [ ] `docker-stop.sh`
- [ ] Configurar health checks
- [ ] Testar deploy local

---

### 4.3 Configurações de Produção
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟢 PRÉ-DEPLOY

**Tarefas:**
- [ ] Criar profiles distintos:
  - [ ] `application-dev.properties`
  - [ ] `application-prod.properties`
- [ ] Configurar produção:
  - [ ] `spring.jpa.show-sql=false`
  - [ ] `server.error.include-stacktrace=never`
  - [ ] `logging.level.root=WARN`
  - [ ] HTTPS only
  - [ ] Connection pool otimizado
- [ ] Configurar variáveis de ambiente obrigatórias
- [ ] Adicionar actuator para health checks
- [ ] Configurar CORS restritivo (apenas frontend)

---

### 4.4 Documentação Final
**Status:** ⬜ Não iniciado  
**Prioridade:** 🟢 PRÉ-DEPLOY

**Tarefas:**
- [ ] Atualizar README.md:
  - [ ] Descrição bem feita
  - [ ] Tecnologias utilizadas
  - [ ] Como rodar localmente
  - [ ] Variáveis de ambiente necessárias
  - [ ] Endpoints principais
  - [ ] Exemplos de requisições
- [ ] Melhorar Swagger:
  - [ ] Descrições em todos os endpoints
  - [ ] Exemplos de request/response
  - [ ] Documentar códigos de erro
  - [ ] Adicionar autenticação no Swagger UI
- [ ] Criar CONTRIBUTING.md (se open source)
- [ ] Criar LICENSE (se aplicável)
- [ ] Adicionar diagrama de arquitetura
- [ ] Adicionar diagrama ER do banco

---

## 📝 NOTAS IMPORTANTES

### Quebra de Contexto
Se houver perda de contexto, verificar:
1. **Branch atual:** `feature/professional-refactoring`
2. **Último checkpoint:** Verificar este arquivo e commits
3. **Fase atual:** Checar checkboxes marcados acima
4. **Arquivos criados:** `git status` e `git log`

### Commits Recomendados
- Fazer commit ao final de cada tarefa principal
- Mensagens descritivas: `feat: implementar DTOs completos`
- Usar conventional commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`

### Testes Durante Desenvolvimento
- Testar cada funcionalidade após implementação
- Rodar aplicação após cada fase completa
- Validar no Swagger/Postman

### Rollback
Se algo quebrar, cada fase pode ser revertida isoladamente:
```bash
git log --oneline
git revert <commit-hash>
```

---

## 🎯 DEFINIÇÃO DE "DONE"

Cada fase está completa quando:
1. ✅ Todos os checkboxes marcados
2. ✅ Código compila sem erros
3. ✅ Aplicação inicia corretamente
4. ✅ Testes (se aplicável) passam
5. ✅ Swagger atualizado
6. ✅ Commit realizado

---

**Início:** 2025-12-09  
**Previsão de Conclusão:** Aproximadamente 4-6 sessões de desenvolvimento  
**Responsável:** AI Assistant + Desenvolvedor
