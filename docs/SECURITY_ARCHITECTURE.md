# 🔐 Arquitetura de Segurança - Spring GameStore

## 📋 Índice
1. [Problema Atual](#problema-atual)
2. [Como Funciona a Segurança](#como-funciona-a-segurança)
3. [Mapeamento de Endpoints](#mapeamento-de-endpoints)
4. [Fluxo de Autenticação](#fluxo-de-autenticação)
5. [Problemas Identificados](#problemas-identificados)
6. [Soluções Recomendadas](#soluções-recomendadas)

---

## 🚨 Problema Atual

### Por que o Swagger pede senha?

O Swagger está pedindo senha porque a configuração de segurança tem **TWO layers de autenticação ATIVADAS SIMULTANEAMENTE**:

1. **JWT Token (Bearer)** - Sistema principal que deveria ser usado
2. **HTTP Basic Auth** - Sistema legado que está causando o popup de senha

**Linha problemática em `BasicSecurityConfig.java`:**
```java
.httpBasic(withDefaults());  // ⚠️ ISTO ESTÁ ATIVANDO O POPUP DE SENHA
```

---

## 🔍 Como Funciona a Segurança

### Arquitetura Atual

```
Cliente/Browser
    ↓
[1. Requisição HTTP]
    ↓
┌─────────────────────────────────────────┐
│  CORS Filter (permite origins)          │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  JWT Auth Filter (JwtAuthFilter)        │
│  - Extrai token do header Authorization │
│  - Valida token JWT                     │
│  - Seta usuário no SecurityContext      │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  HTTP Basic Auth (httpBasic)            │
│  ⚠️ CAUSA O POPUP DE SENHA              │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  Security Filter Chain                  │
│  - Verifica permissões (hasRole)        │
│  - Libera ou bloqueia acesso            │
└─────────────────────────────────────────┘
    ↓
[2. Controller]
    ↓
[3. Service]
    ↓
[4. Repository]
```

### Componentes Principais

#### 1️⃣ **BasicSecurityConfig.java**
- **Função:** Configuração central de segurança
- **Problemas:**
  - Tem JWT + Basic Auth ao mesmo tempo
  - Regras de acesso confusas e redundantes
  - `.anyRequest().authenticated()` muito restritivo

#### 2️⃣ **JwtAuthFilter.java**
- **Função:** Intercepta requisições e valida JWT
- **Fluxo:**
  1. Busca header `Authorization: Bearer <token>`
  2. Extrai token e valida
  3. Carrega usuário e roles
  4. Seta no SecurityContext
- **Status:** ✅ Funciona bem

#### 3️⃣ **JwtService.java**
- **Função:** Gera e valida tokens JWT
- **Configuração:**
  - Secret: Definido em `application.properties`
  - Expiração: 1 hora (3600000ms)
- **Status:** ✅ Funciona bem

#### 4️⃣ **UserDetailsServiceImpl.java**
- **Função:** Carrega usuário do banco
- **Status:** ✅ Funciona bem

---

## 🗺️ Mapeamento de Endpoints

### Endpoints Públicos (Sem Autenticação)
```
✅ POST   /usuarios/cadastrar       - Criar conta
✅ POST   /usuarios/logar           - Login (retorna JWT)
✅ GET    /produtos/**              - Listar produtos (READ ONLY)
✅ GET    /categorias/**            - Listar categorias (READ ONLY)
✅ GET    /swagger-ui/**            - Interface Swagger
✅ GET    /v3/api-docs/**           - OpenAPI docs
⚠️ OPTIONS /**                      - CORS preflight
```

### Endpoints Requerem ADMIN (Role: ROLE_ADMIN)
```
🔒 GET    /usuarios/**              - Listar usuários
🔒 POST   /produtos/**              - Criar produtos
🔒 PUT    /produtos/**              - Atualizar produtos
🔒 DELETE /produtos/**              - Deletar produtos
🔒 POST   /categorias/**            - Criar categorias
🔒 PUT    /categorias/**            - Atualizar categorias
🔒 DELETE /categorias/**            - Deletar categorias
🔒 POST   /igdb/import              - Importar dados IGDB
```

### Endpoints Requerem Usuário Autenticado (Qualquer Role)
```
🔐 PUT    /usuarios/atualizar/{id}  - Atualizar perfil
🔐 POST   /carrinho/**              - Gerenciar carrinho
🔐 GET    /carrinho/**              - Ver carrinho
🔐 POST   /favoritos/**             - Adicionar favorito
🔐 GET    /favoritos/**             - Listar favoritos
🔐 POST   /avaliacoes/**            - Criar avaliação
🔐 PUT    /avaliacoes/{id}          - Atualizar avaliação própria
🔐 DELETE /avaliacoes/{id}          - Deletar avaliação própria
```

**Nota:** A linha `.anyRequest().authenticated()` faz com que QUALQUER endpoint não listado explicitamente precise de autenticação!

---

## 🔄 Fluxo de Autenticação

### 1. Cadastro de Novo Usuário
```
POST /usuarios/cadastrar
Content-Type: application/json

{
  "nome": "João Silva",
  "usuario": "joao@email.com",
  "senha": "senha123",
  "foto": ""
}

Resposta 201:
{
  "id": 1,
  "nome": "João Silva",
  "usuario": "joao@email.com",
  "foto": "",
  "roles": ["ROLE_USER"]
}
```

### 2. Login (Obter Token JWT)
```
POST /usuarios/logar
Content-Type: application/json

{
  "usuario": "joao@email.com",
  "senha": "senha123"
}

Resposta 200:
{
  "id": 1,
  "nome": "João Silva",
  "usuario": "joao@email.com",
  "foto": "",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_USER"]
}
```

### 3. Usar Token em Requisições
```
GET /carrinho
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Resposta 200: { ... dados do carrinho ... }
```

### 4. Autorização no Swagger
1. Acessar: http://localhost:8080/swagger-ui.html
2. **ATUALMENTE:** Popup de senha aparece (HTTP Basic Auth)
3. **DEVERIA:** Clicar no botão 🔓 "Authorize"
4. Colar token no campo: `Bearer <seu-token-aqui>`
5. Clicar "Authorize" → "Close"

---

## ⚠️ Problemas Identificados

### 1. **HTTP Basic Auth Desnecessário**
```java
// BasicSecurityConfig.java (linha ~110)
.httpBasic(withDefaults());  // ❌ REMOVE ISTO
```
**Impacto:**
- Causa popup de senha no browser
- Confunde autenticação JWT
- Swagger fica confuso sobre qual método usar

### 2. **Configuração Redundante de Swagger**
```java
.requestMatchers("/swagger-ui/**").permitAll()
.requestMatchers("/v3/api-docs/**").permitAll()
.requestMatchers("/swagger-resources/**").permitAll()
.requestMatchers("/webjars/**").permitAll()
```
**Problema:** Muito verboso, pode usar padrão único

### 3. **`.anyRequest().authenticated()` Muito Restritivo**
Qualquer endpoint novo automaticamente requer autenticação, mesmo que devesse ser público.

### 4. **Falta de Documentação de Segurança no Swagger**
Swagger não mostra claramente que precisa de JWT Bearer token.

### 5. **Roles Hardcoded**
```java
.hasRole("ADMIN")  // Espera ROLE_ADMIN no banco
```
Se o banco tiver só "ADMIN" (sem ROLE_), não funciona.

### 6. **CORS Muito Permissivo**
```java
@CrossOrigin(origins = "*", allowedHeaders = "*")
```
Presente em TODOS os controllers - redundante e inseguro.

### 7. **Sem Rate Limiting**
Nenhuma proteção contra brute force no `/usuarios/logar`.

### 8. **Senha em Plain Text nos Logs**
Com `show-sql=true`, dados sensíveis podem aparecer nos logs.

---

## ✅ Soluções Recomendadas

### Solução 1: Remover HTTP Basic Auth (URGENTE)

**Arquivo:** `BasicSecurityConfig.java`

**REMOVER:**
```java
.httpBasic(withDefaults());
```

**Resultado:** Swagger não pedirá mais popup de senha!

---

### Solução 2: Simplificar Regras de Segurança

**Substituir bloco confuso por:**

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .cors(withDefaults());

    http.authorizeHttpRequests(auth -> auth
        // Endpoints públicos
        .requestMatchers("/usuarios/logar", "/usuarios/cadastrar").permitAll()
        .requestMatchers("/error/**").permitAll()
        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
        
        // Produtos e Categorias - GET público, modificação só ADMIN
        .requestMatchers(HttpMethod.GET, "/produtos/**", "/categorias/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/produtos/**", "/categorias/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/produtos/**", "/categorias/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/produtos/**", "/categorias/**").hasRole("ADMIN")
        
        // Admin endpoints
        .requestMatchers("/igdb/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.GET, "/usuarios/all").hasRole("ADMIN")
        
        // Endpoints autenticados (qualquer usuário logado)
        .requestMatchers("/carrinho/**", "/favoritos/**", "/avaliacoes/**").authenticated()
        .requestMatchers("/usuarios/atualizar/**").authenticated()
        
        // Preflight CORS
        .requestMatchers(HttpMethod.OPTIONS).permitAll()
        
        // Resto requer autenticação
        .anyRequest().authenticated()
    )
    .authenticationProvider(authenticationProvider())
    .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

---

### Solução 3: Adicionar Configuração de Segurança no Swagger

**Criar novo arquivo:** `SwaggerSecurityConfig.java`

```java
package com.energygames.lojadegames.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerSecurityConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Energy Games API")
                .version("1.0")
                .description("API para loja de games com autenticação JWT"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Insira o token JWT obtido no endpoint /usuarios/logar")));
    }
}
```

---

### Solução 4: Remover @CrossOrigin dos Controllers

**Por quê?** CORS já está configurado globalmente em `BasicSecurityConfig`.

**Ação:** Remover de TODOS os controllers:
```java
@CrossOrigin(origins = "*", allowedHeaders = "*")  // ❌ REMOVER
```

E manter apenas no `BasicSecurityConfig` (já existe):
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedOrigin("http://localhost:5173");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

### Solução 5: Adicionar Endpoint de Health Check Público

```java
@RestController
@RequestMapping("/health")
public class HealthController {
    
    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
```

E adicionar no `BasicSecurityConfig`:
```java
.requestMatchers("/health").permitAll()
```

---

### Solução 6: Melhorar Mensagens de Erro

**Criar:** `AuthenticationEntryPointImpl.java`

```java
package com.energygames.lojadegames.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", System.currentTimeMillis());
        error.put("status", 401);
        error.put("error", "Unauthorized");
        error.put("message", "Token JWT inválido ou ausente. Use /usuarios/logar para obter token.");
        error.put("path", request.getServletPath());
        
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
```

E usar no `BasicSecurityConfig`:
```java
@Autowired
private AuthenticationEntryPointImpl authenticationEntryPoint;

// Dentro do filterChain:
.exceptionHandling(ex -> ex
    .authenticationEntryPoint(authenticationEntryPoint))
```

---

## 📊 Comparação: Antes vs Depois

| Aspecto | Antes (Atual) | Depois (Recomendado) |
|---------|--------------|---------------------|
| **Acesso ao Swagger** | ❌ Pede popup de senha | ✅ Acesso direto |
| **Autenticação** | ⚠️ JWT + Basic Auth | ✅ Apenas JWT |
| **Clareza de Regras** | ❌ Confuso, redundante | ✅ Claro e organizado |
| **CORS** | ⚠️ Duplicado em controllers | ✅ Centralizado |
| **Mensagens de Erro** | ❌ 403 genérico | ✅ JSON explicativo |
| **Swagger Auth** | ❌ Sem instruções | ✅ Com botão Authorize |
| **Segurança** | ⚠️ origins="*" (inseguro) | ✅ Origens específicas |

---

## 🚀 Próximos Passos Sugeridos

### Prioridade ALTA (Fazer Agora)
1. ✅ **Remover `.httpBasic(withDefaults())`** - Resolve problema do Swagger
2. ✅ **Simplificar regras de segurança** - Mais claro e manutenível
3. ✅ **Adicionar SwaggerSecurityConfig** - Documenta autenticação

### Prioridade MÉDIA (Próximas Sprints)
4. 🔄 Remover `@CrossOrigin` duplicado dos controllers
5. 🔄 Adicionar AuthenticationEntryPoint customizado
6. 🔄 Criar health check público
7. 🔄 Adicionar rate limiting no login

### Prioridade BAIXA (Futuro)
8. 📋 Implementar refresh token
9. 📋 Adicionar auditoria de logins
10. 📋 Implementar logout (blacklist de tokens)
11. 📋 Adicionar 2FA para admins

---

## 🧪 Como Testar Após Correções

### 1. Testar Swagger
```bash
# Acessar navegador
http://localhost:8080/swagger-ui.html

# Não deve pedir popup de senha ✅
# Deve mostrar botão "Authorize" com JWT ✅
```

### 2. Testar Cadastro + Login
```bash
# 1. Cadastrar usuário
curl -X POST http://localhost:8080/usuarios/cadastrar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teste",
    "usuario": "teste@email.com",
    "senha": "senha123",
    "foto": ""
  }'

# 2. Fazer login
curl -X POST http://localhost:8080/usuarios/logar \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": "teste@email.com",
    "senha": "senha123"
  }'

# Copiar o token da resposta
```

### 3. Testar Endpoint Protegido
```bash
# Com token
curl http://localhost:8080/carrinho \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"

# Sem token (deve retornar 401)
curl http://localhost:8080/carrinho
```

### 4. Testar Endpoint Público
```bash
# Deve funcionar sem token
curl http://localhost:8080/produtos
```

---

## 📚 Referências e Documentação

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - Decodificar e debugar tokens
- [OpenAPI/Swagger Security](https://swagger.io/docs/specification/authentication/)
- [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture/)

---

## ❓ FAQ

### Por que não usar HTTP Basic Auth?
- Envia credenciais em TODA requisição (menos seguro)
- Causa popup no navegador
- JWT é stateless e mais moderno

### Posso usar JWT + Basic juntos?
- Tecnicamente sim, mas confuso
- Recomendado: escolher UM método

### Como criar usuário ADMIN?
```sql
-- Diretamente no banco
INSERT INTO tb_usuarios (nome, usuario, senha, foto) 
VALUES ('Admin', 'admin@email.com', '$2a$10$HASH_DA_SENHA', '');

INSERT INTO usuario_roles (usuario_id, role) 
VALUES (1, 'ROLE_ADMIN');
```

Ou criar endpoint `/usuarios/promover-admin` (protegido).

### Token expira?
Sim, após 1 hora (configurável em `application.properties`).

### Como renovar token?
Atualmente: fazer novo login.
Futuro: implementar refresh token.

---

**Documento gerado em:** 13 de Dezembro de 2025  
**Versão:** 1.0  
**Autor:** GitHub Copilot  
**Status:** 🔴 Problemas identificados, aguardando correções
