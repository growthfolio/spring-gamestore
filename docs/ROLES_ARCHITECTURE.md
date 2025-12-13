# 🔐 Arquitetura de Roles - Spring GameStore

## 📋 Estado Atual

### Como está Implementado

#### 1. **RoleEnum.java**
```java
public enum RoleEnum {
    ROLE_USER,
    ROLE_ADMIN
}
```

#### 2. **Usuario.java - Persistência**
```java
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
@Column(name = "role")
@Enumerated(EnumType.STRING)  // ⚠️ Persiste como STRING no banco
private Set<RoleEnum> roles = new HashSet<>();
```

#### 3. **Estrutura no Banco de Dados**
```sql
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    role ENUM('ROLE_USER', 'ROLE_ADMIN')  -- ⚠️ Armazenado como STRING
);
```

**Exemplo de dados atuais:**
```sql
SELECT * FROM usuario_roles;
+------------+------------+
| usuario_id | role       |
+------------+------------+
|          1 | ROLE_USER  |  ⚠️ STRING
|          2 | ROLE_ADMIN |  ⚠️ STRING
+------------+------------+
```

---

## ❌ Problemas da Abordagem Atual

### 1. **Armazenamento Ineficiente**
- STRING ocupa mais espaço (10-15 bytes vs 1-4 bytes)
- Índices maiores e mais lentos
- Mais dados transferidos pela rede

### 2. **Sem Flexibilidade**
- MySQL ENUM é limitado e inflexível
- Difícil adicionar novas roles sem ALTER TABLE
- Mudanças no enum podem causar problemas de migração

### 3. **Performance**
- Comparações de string são mais lentas que int
- Cache menos eficiente

### 4. **Manutenibilidade**
- Se mudar o nome da role no código, precisa migrar dados
- Risco de inconsistências entre código e banco

---

## ✅ Solução Recomendada: Persistir como INTEGER

### Opção 1: @Enumerated(EnumType.ORDINAL) - Mais Simples

#### Vantagens
- ✅ Implementação rápida (uma linha de código)
- ✅ Persiste como INT automaticamente
- ✅ Performance melhor

#### Desvantagens
- ⚠️ **PERIGO:** Se reordenar o enum, quebra tudo!
- ⚠️ Adicionar role no meio muda valores existentes

**Exemplo:**
```java
// ANTES
public enum RoleEnum {
    ROLE_USER,    // 0
    ROLE_ADMIN    // 1
}

// DEPOIS (QUEBRA O BANCO!)
public enum RoleEnum {
    ROLE_GUEST,   // 0 ⚠️ Era ROLE_USER!
    ROLE_USER,    // 1 ⚠️ Era ROLE_ADMIN!
    ROLE_ADMIN    // 2
}
```

---

### Opção 2: Enum com Valor Explícito - **RECOMENDADA** ✨

#### Estrutura Proposta

**1. RoleEnum com valores explícitos:**

```java
package com.energygames.lojadegames.enums;

public enum RoleEnum {
    ROLE_USER(1, "Usuário Comum"),
    ROLE_ADMIN(2, "Administrador"),
    ROLE_MODERATOR(3, "Moderador");  // Fácil adicionar novas roles
    
    private final Integer code;
    private final String description;
    
    RoleEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Converter INT → ENUM
    public static RoleEnum fromCode(Integer code) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Código de role inválido: " + code);
    }
    
    // Para Spring Security
    public String getAuthority() {
        return this.name();
    }
}
```

**2. Converter JPA (AttributeConverter):**

```java
package com.energygames.lojadegames.converter;

import com.energygames.lojadegames.enums.RoleEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte RoleEnum para Integer no banco e vice-versa.
 * Persiste apenas o código numérico (1, 2, 3...) ao invés de strings.
 */
@Converter(autoApply = true)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RoleEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public RoleEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return RoleEnum.fromCode(dbData);
    }
}
```

**3. Atualizar Usuario.java:**

```java
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
@Column(name = "role")
@Convert(converter = RoleEnumConverter.class)  // ✅ Usa converter
private Set<RoleEnum> roles = new HashSet<>();
```

**4. Estrutura no Banco (após migração):**

```sql
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    role INT NOT NULL  -- ✅ Inteiro puro
);

-- Dados:
+------------+------+
| usuario_id | role |
+------------+------+
|          1 |    1 |  ✅ INT (ROLE_USER)
|          2 |    2 |  ✅ INT (ROLE_ADMIN)
+------------+------+
```

---

## 🔄 Migração de STRING → INTEGER

### Script de Migração

```sql
-- 1. Criar tabela temporária
CREATE TABLE usuario_roles_temp (
    usuario_id BIGINT NOT NULL,
    role INT NOT NULL
);

-- 2. Migrar dados (STRING → INT)
INSERT INTO usuario_roles_temp (usuario_id, role)
SELECT 
    usuario_id,
    CASE role
        WHEN 'ROLE_USER' THEN 1
        WHEN 'ROLE_ADMIN' THEN 2
        ELSE 0
    END as role
FROM usuario_roles;

-- 3. Verificar migração
SELECT * FROM usuario_roles_temp;

-- 4. Backup da tabela original
RENAME TABLE usuario_roles TO usuario_roles_backup;

-- 5. Renomear nova tabela
RENAME TABLE usuario_roles_temp TO usuario_roles;

-- 6. Adicionar constraints
ALTER TABLE usuario_roles
    ADD CONSTRAINT fk_usuario_roles 
    FOREIGN KEY (usuario_id) REFERENCES tb_usuarios(id) ON DELETE CASCADE;

-- 7. Adicionar índice
CREATE INDEX idx_usuario_roles ON usuario_roles(usuario_id, role);

-- 8. Se tudo OK, remover backup
-- DROP TABLE usuario_roles_backup;
```

---

## 📊 Comparação das Abordagens

| Aspecto | STRING (Atual) | ORDINAL | INTEGER com Converter |
|---------|----------------|---------|----------------------|
| **Espaço no Banco** | ~10-15 bytes | 1-4 bytes | 1-4 bytes |
| **Performance** | ⚠️ Lento | ✅ Rápido | ✅ Rápido |
| **Legibilidade Banco** | ✅ Claro | ❌ Só números | ⚠️ Precisa consultar enum |
| **Segurança** | ✅ Seguro | ❌ Reordenar quebra | ✅ Seguro |
| **Adicionar Roles** | ⚠️ ALTER TABLE | ✅ Fácil | ✅ Fácil |
| **Manutenção** | ⚠️ Difícil | ⚠️ Perigoso | ✅ Fácil |
| **Migração** | - | ⚠️ Complexa | ✅ Controlada |
| **Flexibilidade** | ❌ Baixa | ⚠️ Média | ✅ Alta |

**Recomendação:** **INTEGER com AttributeConverter** ✅

---

## 🛠️ Implementação Passo a Passo

### Passo 1: Atualizar RoleEnum

```java
package com.energygames.lojadegames.enums;

public enum RoleEnum {
    ROLE_USER(1, "Usuário Comum"),
    ROLE_ADMIN(2, "Administrador");
    
    private final Integer code;
    private final String description;
    
    RoleEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static RoleEnum fromCode(Integer code) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Código de role inválido: " + code);
    }
    
    public String getAuthority() {
        return this.name();
    }
}
```

### Passo 2: Criar RoleEnumConverter

```java
package com.energygames.lojadegames.converter;

import com.energygames.lojadegames.enums.RoleEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RoleEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public RoleEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return RoleEnum.fromCode(dbData);
    }
}
```

### Passo 3: Atualizar Usuario.java

```java
// ANTES:
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
@Column(name = "role")
@Enumerated(EnumType.STRING)  // ❌ REMOVER
private Set<RoleEnum> roles = new HashSet<>();

// DEPOIS:
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
@Column(name = "role")
@Convert(converter = RoleEnumConverter.class)  // ✅ ADICIONAR
private Set<RoleEnum> roles = new HashSet<>();
```

### Passo 4: Limpar Banco e Recriar (Desenvolvimento)

```bash
# 1. Parar aplicação
# 2. Dropar banco
mysql -uroot -proot -e "DROP DATABASE db_energygames; CREATE DATABASE db_energygames;"

# 3. Reiniciar aplicação (Hibernate vai recriar com INT)
./mvnw spring-boot:run
```

### Passo 5: Verificar

```sql
-- Ver estrutura
DESCRIBE usuario_roles;
-- role | int | YES | | NULL | |  ✅

-- Cadastrar usuário e verificar
SELECT * FROM usuario_roles;
+------------+------+
| usuario_id | role |
+------------+------+
|          1 |    1 |  ✅ INTEGER!
+------------+------+
```

---

## 🎯 Vantagens da Solução Final

### 1. **Performance**
```
STRING: SELECT * WHERE role = 'ROLE_ADMIN'  (10-15 bytes, comparação string)
INT:    SELECT * WHERE role = 2             (4 bytes, comparação numérica)
```

### 2. **Espaço em Disco**
```
1 milhão de usuários:
- STRING: ~15 MB
- INT:    ~4 MB
- Economia: 73%
```

### 3. **Flexibilidade**
```java
// Adicionar nova role = só adicionar no enum
ROLE_MODERATOR(3, "Moderador"),
ROLE_VIP(4, "VIP"),
ROLE_BANNED(99, "Banido")
// ✅ Sem ALTER TABLE, sem quebrar código existente
```

### 4. **Segurança**
```java
// Códigos explícitos = sem risco de reordenação
ROLE_USER(1, "..."),    // Sempre será 1
ROLE_ADMIN(2, "...")    // Sempre será 2
```

### 5. **Manutenibilidade**
```java
// Mudar nome no código não afeta banco
ROLE_USER → ROLE_REGULAR_USER  // ✅ Código continua 1
```

---

## ⚠️ Cuidados Importantes

### 1. **Nunca Reutilizar Códigos**
```java
// ❌ NUNCA FAZER:
// ROLE_GUEST(1, "...")  // Removido
// ROLE_USER(1, "...")   // Reutilizando código 1

// ✅ FAZER:
// ROLE_GUEST(1, "...")  // Mantém código 1 mesmo se não usar mais
ROLE_USER(2, "...")
ROLE_ADMIN(3, "...")
```

### 2. **Documentar Códigos**
```java
/**
 * Códigos de roles do sistema.
 * 
 * ATENÇÃO: Nunca remover ou alterar códigos existentes!
 * Novos valores devem ser sequenciais.
 * 
 * Histórico:
 * - 1: ROLE_USER (desde v1.0)
 * - 2: ROLE_ADMIN (desde v1.0)
 * - 3: ROLE_MODERATOR (desde v2.0)
 */
public enum RoleEnum {
    ROLE_USER(1, "Usuário Comum"),
    ROLE_ADMIN(2, "Administrador"),
    ROLE_MODERATOR(3, "Moderador");
    // ...
}
```

### 3. **Testes**
```java
@Test
void testRoleEnumConverter() {
    RoleEnumConverter converter = new RoleEnumConverter();
    
    // Converter para banco
    assertEquals(1, converter.convertToDatabaseColumn(RoleEnum.ROLE_USER));
    assertEquals(2, converter.convertToDatabaseColumn(RoleEnum.ROLE_ADMIN));
    
    // Converter do banco
    assertEquals(RoleEnum.ROLE_USER, converter.convertToEntityAttribute(1));
    assertEquals(RoleEnum.ROLE_ADMIN, converter.convertToEntityAttribute(2));
    
    // Código inválido
    assertThrows(IllegalArgumentException.class, 
        () -> RoleEnum.fromCode(999));
}
```

---

## 🚀 Próximos Passos

1. ✅ Criar `RoleEnumConverter`
2. ✅ Atualizar `RoleEnum` com códigos
3. ✅ Modificar `Usuario.java`
4. ✅ Limpar banco (desenvolvimento)
5. ✅ Testar criação de usuário
6. ✅ Verificar queries e joins
7. ✅ Atualizar documentação

---

## 📚 Referências

- [JPA AttributeConverter](https://docs.oracle.com/javaee/7/api/javax/persistence/AttributeConverter.html)
- [Best Practices for Enum Storage](https://vladmihalcea.com/the-best-way-to-map-an-enum-type-with-jpa-and-hibernate/)
- [Spring Security with Custom Roles](https://www.baeldung.com/spring-security-custom-voter)

---

**Documento gerado em:** 13 de Dezembro de 2025  
**Versão:** 1.0  
**Status:** 📋 Aguardando implementação
