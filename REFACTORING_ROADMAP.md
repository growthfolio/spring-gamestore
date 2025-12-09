# 🎯 ROADMAP DE REFATORAÇÃO
**Branch:** `feature/professional-refactoring`  
**Objetivo:** Transformar o código de nível acadêmico para nível de portfólio

---

## 📊 PROGRESSO GERAL

- [x] **FASE 1** - Fundação Crítica (5/5) ✅
- [x] **FASE 2** - Qualidade e Segurança (4/4) ✅
- [x] **FASE 3** - Features de Negócio (3/3) ✅
- [ ] **FASE 4** - Pré-Produção (0/4)

**Status Atual:** 🎉 FASE 3 Completa - Sistema Profissional!  
**Última Atualização:** 2025-12-09 14:00
**Total de Commits:** 8 commits estruturados
**Estatísticas:** 65 arquivos, +4.317 linhas, -243 linhas, 66 classes Java

**Commits por Fase:**
- **FASE 1:** d553970 (Fundação Crítica)
- **FASE 2:** 914987e (Paginação), cbc0a46 (Logging), 5705f4f (Validações), 4deb628 (Docs)
- **FASE 3:** 277cecb (Avaliações), 242f6d5 (Favoritos), 08820ce (Carrinho)

---

## ✅ FASE 1 - FUNDAÇÃO CRÍTICA ✅ COMPLETA

### 1.1 Implementar DTOs Completos
**Status:** ✅ Concluído  
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
**Status:** ✅ Concluído  
**Prioridade:** 🔴 CRÍTICA

**Tarefas:**
- [x] Criar package `exception`
- [x] Criar exceções customizadas:
  - [x] `ResourceNotFoundException`
  - [x] `BusinessException`
  - [x] `UnauthorizedException`
  - [x] `DuplicateResourceException`
- [x] Criar DTOs de erro:
  - [x] `ErrorResponse`
  - [x] `ValidationErrorResponse`
- [x] Criar `GlobalExceptionHandler` com `@RestControllerAdvice`
- [x] Implementar handlers para:
  - [x] `ResourceNotFoundException` → 404
  - [x] `BusinessException` → 400
  - [x] `UnauthorizedException` → 401
  - [x] `AccessDeniedException` → 403
  - [x] `MethodArgumentNotValidException` → 400 (validação)
  - [x] `Exception` → 500 (genérico)
- [x] Adicionar logs estruturados em cada handler

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
**Status:** ✅ Concluído  
**Prioridade:** 🔴 CRÍTICA

**Tarefas:**
- [x] Criar `CategoriaService` e `CategoriaServiceImpl`
  - [x] Mover lógica do `CategoriaController` para service
  - [x] Adicionar validações de negócio
  - [x] Validar se categoria está ativa antes de associar produtos
  - [x] Implementar soft delete (marcar como inativo)
- [x] Criar `ProdutoService` e `ProdutoServiceImpl`
  - [x] Mover lógica do `ProdutoController` para service
  - [x] Validar existência de categoria
  - [x] Validar estoque antes de operações
  - [x] Calcular preço com desconto
  - [x] Implementar soft delete
- [x] Refatorar `UsuarioService`
  - [x] Separar interface e implementação
  - [x] Adicionar método `obterUsuarioAutenticado()`
  - [x] Melhorar validações
- [x] Atualizar todos os controllers para usar services
- [x] Remover acesso direto a repositories nos controllers

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
**Status:** ✅ Concluído  
**Prioridade:** 🔴 CRÍTICA (Segurança)

**Tarefas:**
- [x] Remover secret hardcoded de `JwtService`
- [x] Adicionar propriedades no `application.properties`:
  - [x] `jwt.secret=${JWT_SECRET:default-dev-secret}`
  - [x] `jwt.expiration=${JWT_EXPIRATION:3600000}`
- [x] Injetar valores com `@Value` no `JwtService`
- [x] Criar `.env.example` com variáveis necessárias
- [x] Atualizar `.gitignore` para ignorar `.env`
- [x] Adicionar validação: secret deve ter mínimo de 256 bits

**Arquivos a Modificar:**
```
src/main/java/com/energygames/lojadegames/security/JwtService.java
src/main/resources/application.properties
.env.example (criar)
.gitignore (atualizar)
```

---

### 1.5 Sistema de Roles
**Status:** ✅ Concluído  
**Prioridade:** 🔴 CRÍTICA (Segurança)

**Tarefas:**
- [x] Criar `RoleEnum` (ROLE_USER, ROLE_ADMIN)
- [x] Adicionar campo `roles` em `Usuario`:
  - [x] `@ElementCollection` com `Set<RoleEnum>`
  - [x] Inicializar com ROLE_USER por padrão
- [x] Atualizar `UserDetailsImpl`:
  - [x] Implementar `getAuthorities()` corretamente
  - [x] Converter roles para `GrantedAuthority`
- [x] Atualizar `BasicSecurityConfig`:
  - [x] Proteger endpoints de admin com `@PreAuthorize("hasRole('ADMIN')")`
  - [x] POST/PUT/DELETE de categorias → ADMIN
  - [x] POST/PUT/DELETE de produtos → ADMIN
  - [x] GET público
- [x] Adicionar `@EnableMethodSecurity` na configuração
- [x] Atualizar cadastro: permitir criar admin via flag opcional

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

## ✅ FASE 2 - QUALIDADE E SEGURANÇA ✅ COMPLETA

### 2.1 Corrigir Double → BigDecimal
**Status:** ✅ Concluído  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [x] Alterar tipo de `preco` em `Produto`: `Double` → `BigDecimal`
- [x] Alterar tipo de `desconto` em `Produto`: `Double` → `BigDecimal`
- [x] Adicionar validações:
  - [x] `@DecimalMin("0.01")` para preço
  - [x] `@DecimalMin("0")` e `@DecimalMax("100")` para desconto
  - [x] `@Column(precision=10, scale=2)` para preço
- [x] Atualizar cálculos de preço com desconto
- [x] Atualizar todos os DTOs relacionados
- [x] Usar `RoundingMode.HALF_UP` em divisões

**Impacto:** Schema do banco será alterado (campo de tipo DECIMAL)

---

### 2.2 Implementar Paginação
**Status:** ✅ Concluído  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [x] Atualizar método `getAll()` de `CategoriaController`:
  - [x] Adicionar parâmetros: `page`, `size`, `sort`
  - [x] Retornar `Page<CategoriaResponseDTO>`
- [x] Atualizar método `getAll()` de `ProdutoController`:
  - [x] Adicionar parâmetros: `page`, `size`, `sort`
  - [x] Retornar `Page<ProdutoResponseDTO>`
  - [x] Adicionar filtros: `nome`, `categoriaId`
- [x] Criar specifications com lambdas para queries dinâmicas
- [x] Atualizar repositories para usar `JpaSpecificationExecutor`
- [x] Configurar tamanho padrão de página: 20 itens
- [x] Adicionar metadata de paginação nas respostas (Page<>)

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
**Status:** ✅ Concluído  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [x] Adicionar SLF4J/Logback (já vem com Spring Boot)
- [x] Adicionar logs em todos os services:
  - [x] `log.info()` para operações importantes
  - [x] `log.warn()` para validações falhadas
  - [x] `log.error()` para exceções
- [x] Adicionar logs de segurança:
  - [x] Login bem-sucedido
  - [x] Login falhado
  - [x] Acesso negado
- [x] Criar `logback-spring.xml` customizado:
  - [x] Pattern com timestamp, level, classe, mensagem
  - [x] Arquivo rotativo de logs (10MB, 30 dias)
  - [x] Logs separados: application.log e error.log
  - [x] Console colorido para desenvolvimento
- [x] Adicionar logs/ ao .gitignore

**Arquivos a Criar:**
```
src/main/resources/
└── logback-spring.xml
```

---

### 2.4 Validações de Negócio
**Status:** ✅ Concluído  
**Prioridade:** 🟠 ALTA

**Tarefas:**
- [x] Categoria:
  - [x] Não permitir deletar categoria com produtos ativos
  - [x] Validações em DTOs (@Pattern, @Size, @NotBlank)
  - [x] Validar se está ativa antes de associar produtos
- [x] Produto:
  - [x] Validar se categoria existe e está ativa
  - [x] Validar se desconto é coerente com preço (0-100%)
  - [x] Não permitir estoque negativo (@Min)
  - [x] Validar data de lançamento (@PastOrPresent)
  - [x] Validar preço final positivo após desconto
  - [x] @Size em todos os campos textuais
- [x] Usuario:
  - [x] Email único (validado no service)
  - [x] Senha forte com regex (maiúscula+minúscula+número)
  - [x] Não permitir alterar email para um já existente

---

### 2.5 Testes Unitários Básicos
**Status:** ⬜ Não iniciado (FASE 3)  
**Prioridade:** 🟡 MÉDIA

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

## ✅ FASE 3 - FEATURES DE NEGÓCIO ✅ COMPLETA

### 3.1 Sistema de Avaliações
**Status:** ✅ Concluído (Commit: 277cecb)  
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
**Status:** ✅ Concluído (Commit: 242f6d5)  
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
**Status:** ✅ Concluído (Commit: 08820ce)  
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
