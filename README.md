# 🎮 Spring GameStore - API RESTful para E-commerce de Jogos

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Sobre o Projeto

API RESTful para gerenciamento de loja de jogos, implementando **padrão de arquitetura** , **segurança JWT**, **migrations com Flyway** e **documentação Swagger**. O projeto demonstra boas práticas de desenvolvimento backend com **Spring Boot 3**, **validações robustas**, **logs estruturados** e **containerização Docker**.

### ✨ Funcionalidades Principais

- ✅ **Autenticação e Autorização**: JWT com roles (USER/ADMIN)
- ✅ **Gestão de Produtos**: CRUD completo com filtros avançados e paginação
- ✅ **Sistema de Categorias**: Organização de produtos por categorias ativas
- ✅ **Sistema de Avaliações**: Notas (1-5) e comentários por usuário
- ✅ **Lista de Favoritos**: Produtos favoritos por usuário
- ✅ **Carrinho de Compras**: Gestão de itens com cálculo de subtotais e descontos
- ✅ **Migrations Flyway**: Versionamento de schema do banco de dados
- ✅ **Validações de Negócio**: Regras complexas com Bean Validation
- ✅ **Logs Estruturados**: Logback com rotação de arquivos
- ✅ **Documentação API**: Swagger/OpenAPI 3.0

## 🛠️ Stack Tecnológico

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.0** - Framework principal
- **Spring Security 6** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM

### Database & Migrations
- **MySQL 8.0** - Banco de dados relacional
- **Flyway 9.22** - Versionamento de schema

### Segurança
- **JWT (JJWT 0.11.5)** - Tokens de autenticação
- **BCrypt** - Hash de senhas

### Validação & Documentação
- **Bean Validation** - Validações de entrada
- **Swagger/OpenAPI 3.0** - Documentação interativa
- **Springdoc OpenAPI 2.0.2** - Integração Swagger

### DevOps & Qualidade
- **Docker & Docker Compose** - Containerização
- **Maven** - Gerenciamento de dependências
- **Logback** - Sistema de logs com rotação
- **SonarQube** - Análise de qualidade de código

## 📁 Estrutura do Projeto

```
spring-gamestore/
├── src/main/java/com/energygames/lojadegames/
│   ├── configuration/           # Configurações gerais
│   │   └── SwaggerConfig.java  # Documentação OpenAPI
│   ├── controller/             # REST Controllers
│   │   ├── CategoriaController.java
│   │   ├── ProdutoController.java
│   │   ├── UsuarioController.java
│   │   ├── AvaliacaoController.java
│   │   ├── FavoritoController.java
│   │   └── CarrinhoController.java
│   ├── dto/                    # Data Transfer Objects
│   │   ├── mapper/            # Conversores Entity-DTO
│   │   ├── request/           # DTOs de entrada
│   │   └── response/          # DTOs de saída
│   ├── exception/             # Tratamento de exceções
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── BusinessRuleException.java
│   │   └── ...
│   ├── model/                 # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Categoria.java
│   │   ├── Produto.java
│   │   ├── Avaliacao.java
│   │   ├── Favorito.java
│   │   └── CarrinhoItem.java
│   ├── repository/            # Repositórios JPA
│   │   ├── UsuarioRepository.java
│   │   ├── CategoriaRepository.java
│   │   ├── ProdutoRepository.java
│   │   ├── AvaliacaoRepository.java
│   │   ├── FavoritoRepository.java
│   │   └── CarrinhoRepository.java
│   ├── security/              # Segurança JWT
│   │   ├── BasicSecurityConfig.java
│   │   ├── JwtAuthFilter.java
│   │   ├── JwtService.java
│   │   └── UserDetailsServiceImpl.java
│   ├── service/               # Lógica de negócio
│   │   ├── impl/             # Implementações
│   │   ├── UsuarioService.java
│   │   ├── CategoriaService.java
│   │   ├── ProdutoService.java
│   │   ├── AvaliacaoService.java
│   │   ├── FavoritoService.java
│   │   └── CarrinhoService.java
│   └── enums/                # Enumerações
│       └── RoleEnum.java
├── src/main/resources/
│   ├── db/migration/         # Scripts Flyway
│   │   └── V1__initial_schema.sql
│   ├── application.properties
│   └── logback-spring.xml   # Configuração de logs
├── docker-compose.yml        # Orquestração de containers
├── Dockerfile               # Imagem Docker da aplicação
├── pom.xml                  # Dependências Maven
└── README.md
```

## 🚀 Guia de Instalação

### Pré-requisitos

- **Java 17** ou superior
- **Maven 3.8+**
- **MySQL 8.0** (ou use Docker Compose)
- **Docker & Docker Compose** (opcional, mas recomendado)

### Opção 1: Usando Docker Compose (Recomendado)

1. **Clone o repositório**
```bash
git clone https://github.com/growthfolio/spring-gamestore.git
cd spring-gamestore
```

2. **Configure as variáveis de ambiente**
```bash
cp .env.example .env
# Edite o arquivo .env com suas configurações
```

3. **Inicie os containers**
```bash
docker-compose up -d
```

A aplicação estará disponível em `http://localhost:8080`

### Opção 2: Instalação Manual

1. **Clone o repositório**
```bash
git clone https://github.com/growthfolio/spring-gamestore.git
cd spring-gamestore
```

2. **Configure o MySQL**
```sql
CREATE DATABASE db_lojadegames CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Configure o `application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_lojadegames?createDatabaseIfNotExist=true
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

4. **Execute as migrations do Flyway**
```bash
./mvnw flyway:migrate
```

5. **Compile e execute o projeto**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 📚 Documentação da API

Após iniciar a aplicação, acesse a documentação interativa do Swagger:

```
http://localhost:8080/swagger-ui.html
```

### Exemplos de Requisições

#### 1. Cadastro de Usuário
```http
POST /usuarios/cadastrar
Content-Type: application/json

{
  "nome": "João Silva",
  "usuario": "joao@email.com",
  "senha": "senha123",
  "foto": "https://i.imgur.com/exemplo.jpg"
}
```

#### 2. Login
```http
POST /usuarios/logar
Content-Type: application/json

{
  "usuario": "joao@email.com",
  "senha": "senha123"
}
```

#### 3. Criar Produto (requer autenticação)
```http
POST /produtos
Authorization: Bearer {seu_token_jwt}
Content-Type: application/json

{
  "nome": "The Legend of Zelda: Breath of the Wild",
  "descricao": "Aventura épica em mundo aberto",
  "preco": 299.99,
  "desconto": 10.00,
  "estoque": 50,
  "plataforma": "Nintendo Switch",
  "desenvolvedor": "Nintendo EPD",
  "publisher": "Nintendo",
  "dataLancamento": "2017-03-03",
  "imagens": ["https://exemplo.com/imagem1.jpg"],
  "ativo": true,
  "categoria": {
    "id": 1
  }
}
```

#### 4. Buscar Produtos com Filtros
```http
GET /produtos?nome=zelda&plataforma=Nintendo%20Switch&page=0&size=10&sort=nome,asc
```

#### 5. Adicionar Avaliação
```http
POST /avaliacoes
Authorization: Bearer {seu_token_jwt}
Content-Type: application/json

{
  "nota": 5,
  "comentario": "Jogo incrível! Melhor experiência em mundo aberto.",
  "produtoId": 1
}
```

#### 6. Adicionar ao Carrinho
```http
POST /carrinho
Authorization: Bearer {seu_token_jwt}
Content-Type: application/json

{
  "produtoId": 1,
  "quantidade": 2
}
```

## 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes com coverage
./mvnw clean test jacoco:report

# Pular testes durante o build
./mvnw clean install -DskipTests
```

## 📊 Monitoramento e Logs

Os logs da aplicação são armazenados em:
- **Console**: Logs em tempo real durante desenvolvimento
- **Arquivo**: `/logs/spring-gamestore.log` (rotação automática a cada 10MB, mantém 30 dias)

### Níveis de Log
- **ERROR**: Erros críticos da aplicação
- **WARN**: Avisos e situações anormais
- **INFO**: Informações importantes de operação
- **DEBUG**: Detalhes de debug (apenas em desenvolvimento)

## 🔒 Segurança

### Autenticação JWT
- **Token válido por**: 1 hora (configurável via `JWT_EXPIRATION`)
- **Algoritmo**: HS256
- **Senha**: Hash BCrypt com salt

### Roles e Permissões
- **USER**: Acesso a endpoints de consulta e operações pessoais
- **ADMIN**: Acesso completo, incluindo gestão de produtos e categorias

### Endpoints Públicos
- `POST /usuarios/cadastrar` - Registro de usuário
- `POST /usuarios/logar` - Login
- `GET /swagger-ui.html` - Documentação

### Endpoints Protegidos
Requerem header `Authorization: Bearer {token}`

## 🐳 Docker

### Construir imagem manualmente
```bash
docker build -t gamestore-api .
```

### Executar container
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/db_lojadegames \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  gamestore-api
```

### Docker Compose (Recomendado)
```bash
# Iniciar todos os serviços
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Parar serviços
docker-compose down

# Limpar volumes (CUIDADO: apaga dados do banco)
docker-compose down -v
```

## 🔄 Migrations com Flyway

### Estrutura de Migrations
```
src/main/resources/db/migration/
└── V1__initial_schema.sql
```

### Convenções de Nomenclatura
- **Versão**: `V{número}__descrição.sql` (ex: `V1__initial_schema.sql`)
- **Repeatable**: `R__script_repetível.sql`

### Comandos Flyway
```bash
# Ver status das migrations
./mvnw flyway:info

# Executar migrations pendentes
./mvnw flyway:migrate

# Limpar banco (CUIDADO: apaga tudo)
./mvnw flyway:clean

# Validar migrations
./mvnw flyway:validate
```

## 📈 Qualidade de Código

### SonarQube
O projeto está integrado com SonarQube para análise de qualidade. Veja `QUICKSTART_SONAR_SYNC.md` para instruções.

### Boas Práticas Implementadas
- ✅ DTOs para separação de camadas
- ✅ Service Layer para lógica de negócio
- ✅ Repository Pattern para acesso a dados
- ✅ Exception Handling centralizado
- ✅ Validações com Bean Validation
- ✅ Logs estruturados
- ✅ Migrations versionadas
- ✅ Documentação OpenAPI
- ✅ Código organizado e coeso

## 🎯 Roadmap de Funcionalidades

### Implementado ✅
- [x] Sistema de autenticação JWT
- [x] Gestão de usuários com roles
- [x] CRUD de categorias
- [x] CRUD de produtos com filtros
- [x] Sistema de avaliações (notas e comentários)
- [x] Lista de favoritos
- [x] Carrinho de compras
- [x] Migrations Flyway
- [x] Docker Compose
- [x] Documentação Swagger

### Próximas Features 🚀
- [ ] Processamento de pedidos
- [ ] Integração com gateway de pagamento
- [ ] Sistema de cupons de desconto
- [ ] Notificações por email
- [ ] Dashboard administrativo
- [ ] Relatórios de vendas
- [ ] Sistema de recomendações
- [ ] Wishlist pública
- [ ] Testes de integração completos

## 💡 Principais Conceitos Aplicados

### 🏗️ Arquitetura em Camadas
- **Controller**: Recebe requisições HTTP e retorna respostas
- **Service**: Contém lógica de negócio e validações
- **Repository**: Acesso e persistência de dados
- **Model**: Entidades JPA mapeadas para o banco

### 🛡️ Segurança
- **JWT**: Autenticação stateless com tokens
- **BCrypt**: Hash seguro de senhas
- **CORS**: Controle de acesso de origens cruzadas
- **HTTPS**: Comunicação segura (configurável para produção)

### 💾 Persistência
- **JPA/Hibernate**: ORM para mapeamento objeto-relacional
- **Flyway**: Migrations versionadas
- **Transactions**: Garantia de consistência (ACID)
- **Lazy/Eager Loading**: Otimização de queries

### 📊 Performance
- **Paginação**: Consultas otimizadas com Spring Data
- **Índices**: Otimização de queries no banco
- **Caching**: Second-level cache do Hibernate
- **Connection Pool**: HikariCP para gerenciamento de conexões

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autor

**Energy Games Team**

- GitHub: [@felipemacedo1](https://github.com/felipemacedo1)
- LinkedIn: [Felipe Macedo](https://linkedin.com/in/felipemacedo1)

## 📞 Suporte

Para dúvidas ou sugestões:
- Abra uma [issue](https://github.com/felipemacedo1/spring-gamestore/issues)
- Entre em contato: contato@energygames.com

---

⭐ Se este projeto foi útil para você, considere dar uma estrela!

**Desenvolvido com ❤️ usando Spring Boot**
- **Price management:** Gestão de preços e promoções

### 🔐 Security Implementation
- **JWT Authentication:** Autenticação baseada em tokens
- **Role-based access:** Controle de acesso por perfis
- **Password encryption:** Criptografia de senhas
- **CORS configuration:** Configuração para frontend
- **API security:** Proteção de endpoints sensíveis

### 📊 Data Management
- **JPA relationships:** Relacionamentos OneToMany/ManyToOne
- **Query optimization:** Consultas otimizadas
- **Transaction management:** Controle transacional
- **Data validation:** Validação de dados de entrada
- **Error handling:** Tratamento consistente de erros

## 🧠 Conceitos Técnicos Estudados

### 1. **Entity Relationships**
```java
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Category categoria;
    
    // Constructors, getters, setters
}

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nome;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Game> games = new ArrayList<>();
}
```

### 2. **REST Controller Design**
```java
@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameController {
    
    @Autowired
    private GameService gameService;
    
    @GetMapping
    public ResponseEntity<List<Game>> getAll() {
        return ResponseEntity.ok(gameService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Game> getById(@PathVariable Long id) {
        return gameService.findById(id)
            .map(game -> ResponseEntity.ok(game))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Game> create(@Valid @RequestBody Game game) {
        Game savedGame = gameService.save(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGame);
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Game>> getByCategory(@PathVariable String categoria) {
        List<Game> games = gameService.findByCategoriaNomeContainingIgnoreCase(categoria);
        return ResponseEntity.ok(games);
    }
}
```

### 3. **Service Layer**
```java
@Service
public class GameService {
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private CategoryService categoryService;
    
    public List<Game> findAll() {
        return gameRepository.findAll();
    }
    
    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }
    
    @Transactional
    public Game save(Game game) {
        // Validar categoria
        if (game.getCategoria() != null && game.getCategoria().getId() != null) {
            Category categoria = categoryService.findById(game.getCategoria().getId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
            game.setCategoria(categoria);
        }
        
        return gameRepository.save(game);
    }
    
    public List<Game> findByCategoriaNomeContainingIgnoreCase(String categoria) {
        return gameRepository.findByCategoriaNomeContainingIgnoreCase(categoria);
    }
}
```

## 🚧 Desafios Enfrentados
1. **Data modeling:** Modelagem eficiente de relacionamentos
2. **Performance:** Otimização de queries com JPA
3. **Security:** Implementação robusta de autenticação
4. **Error handling:** Tratamento consistente de exceções
5. **Testing:** Cobertura adequada de testes

## 📚 Recursos Utilizados
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
- [E-commerce Best Practices](https://www.baeldung.com/spring-boot-ecommerce)
- [Generation Brasil Bootcamp](https://brazil.generation.org/) - Bootcamp onde o projeto foi desenvolvido

## 📈 Próximos Passos
- [ ] Implementar sistema de carrinho de compras
- [ ] Adicionar sistema de avaliações
- [ ] Criar sistema de recomendações
- [ ] Implementar processamento de pagamentos
- [ ] Adicionar sistema de cupons de desconto
- [ ] Criar dashboard administrativo

## 🔗 Projetos Relacionados
- [React GameStore Front](../react-gamestore-front/) - Frontend da aplicação

---

**Desenvolvido por:** Felipe Macedo  
**Contato:** contato.dev.macedo@gmail.com  
**GitHub:** [FelipeMacedo](https://github.com/felipemacedo1)  
**LinkedIn:** [felipemacedo1](https://linkedin.com/in/felipemacedo1)

> 💡 **Reflexão:** Este projeto consolidou conhecimentos em desenvolvimento de APIs REST para e-commerce. A implementação de relacionamentos JPA e sistema de autenticação proporcionou experiência prática em arquiteturas backend robustas.