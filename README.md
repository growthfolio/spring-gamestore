# 🎮 Spring GameStore - API de Gestão de Loja de Jogos

## 🎯 Objetivo de Aprendizado
API RESTful desenvolvida para estudar **gestão de e-commerce** e **arquitetura Spring Boot**. Implementa sistema completo de loja de jogos com **gerenciamento de categorias**, **inventário de produtos** e **autenticação de usuários**, aplicando boas práticas de desenvolvimento backend.

## 🛠️ Tecnologias Utilizadas
- **Framework:** Spring Boot, Spring Data JPA
- **Segurança:** Spring Security, JWT
- **Banco de dados:** PostgreSQL
- **Testes:** JUnit, Mockito
- **Qualidade:** SonarCloud integration
- **Documentação:** Swagger/OpenAPI
- **Deploy:** Docker, Heroku

## 🚀 Demonstração
```json
// POST /api/games - Criar novo jogo
{
  "nome": "The Legend of Zelda: Breath of the Wild",
  "descricao": "Aventura épica em mundo aberto",
  "preco": 299.99,
  "plataforma": "Nintendo Switch",
  "categoria": {
    "id": 1,
    "nome": "Aventura"
  }
}

// GET /api/games - Listar jogos
{
  "content": [
    {
      "id": 1,
      "nome": "The Legend of Zelda: Breath of the Wild",
      "preco": 299.99,
      "categoria": "Aventura",
      "disponivel": true
    }
  ]
}
```

## 📁 Estrutura do Projeto
```
spring-gamestore/
├── src/main/java/
│   ├── controller/               # REST Controllers
│   │   ├── GameController.java   # Endpoints de jogos
│   │   ├── CategoryController.java # Endpoints de categorias
│   │   └── UserController.java   # Endpoints de usuários
│   ├── model/                    # Entidades JPA
│   │   ├── Game.java            # Entidade Jogo
│   │   ├── Category.java        # Entidade Categoria
│   │   └── User.java            # Entidade Usuário
│   ├── repository/               # Repositórios JPA
│   ├── service/                  # Lógica de negócio
│   ├── security/                 # Configurações de segurança
│   └── config/                   # Configurações gerais
├── src/test/                     # Testes automatizados
└── target/                       # Arquivos compilados
```

## 💡 Principais Aprendizados

### 🛍️ E-commerce Architecture
- **Product management:** CRUD completo de produtos
- **Category organization:** Hierarquia de categorias
- **Inventory control:** Controle de estoque
- **Search functionality:** Busca e filtros avançados

### 🔐 Spring Security & JWT
- **Stateless authentication:** Tokens JWT
- **Password encryption:** BCrypt hashing
- **Role-based access:** Controle de permissões
- **Security filters:** Cadeia de filtros customizados

### 🗄️ Spring Data JPA
- **Entity relationships:** OneToMany, ManyToOne
- **Query methods:** Derived queries e @Query
- **Pagination:** Paginação e ordenação
- **Transactions:** Gestão transacional

### 🧪 Testing Best Practices
- **Unit tests:** Testes isolados com Mockito
- **Integration tests:** Testes com banco H2
- **Test coverage:** Análise de cobertura
- **TDD approach:** Test-Driven Development

## 🔄 Guias e Documentação

- [📖 Roadmap de Refatoração](REFACTORING_ROADMAP.md) - Planejamento detalhado das melhorias
- [🧩 Guia de Submódulos Git](SUBMODULES.md) - Como gerenciar o frontend React
- [⚛️ Requisitos do Frontend](FRONTEND_REQUIREMENTS.md) - Especificações da interface

## ⚙️ Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- PostgreSQL 12+ (ou usar Docker)

### Configuração Rápida

1. **Clone o repositório:**
```bash
git clone https://github.com/growthfolio/spring-gamestore.git
cd spring-gamestore
```

2. **Configure as variáveis de ambiente:**
```bash
# Copie o arquivo de exemplo e edite conforme necessário
cp .env.example .env
```

3. **Usando Docker (Recomendado):**
```bash
# Inicia todos os serviços (backend + PostgreSQL)
docker-compose up -d

# Verificar logs
docker-compose logs -f app
```

4. **Ou executar localmente:**
```bash
# Configure PostgreSQL localmente e ajuste application.properties
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`

### 🧪 Executar Testes

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