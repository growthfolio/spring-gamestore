# 🎮 GameStore - Fullstack com Git Submodules

## 📁 Estrutura do Projeto

```
spring-gamestore/                    # Repositório backend (este)
├── src/                            # Código Spring Boot
├── frontend/                       # 🔗 Submódulo Git
│   └── (react-gamestore-front)    # Repositório frontend separado
├── .gitmodules                     # Configuração do submódulo
└── docker-compose.yml
```

## 🔗 Trabalhando com Submódulos Git

### 🆕 Clone Inicial (primeira vez)

```bash
# Opção 1: Clone com submódulos (recomendado)
git clone --recurse-submodules https://github.com/growthfolio/spring-gamestore.git

# Opção 2: Clone normal + inicializar submódulos depois
git clone https://github.com/growthfolio/spring-gamestore.git
cd spring-gamestore
git submodule init
git submodule update
```

### 🔄 Atualizar Frontend (submódulo)

```bash
# Entrar no submódulo
cd frontend

# Fazer alterações no código
# ... editar arquivos ...

# Commit no repositório do frontend
git add .
git commit -m "feat: nova funcionalidade"
git push origin main

# Voltar para o backend
cd ..

# Atualizar referência do submódulo no backend
git add frontend
git commit -m "chore: atualizar referência do frontend"
git push origin feature/professional-refactoring
```

### ⬆️ Puxar Atualizações

```bash
# Atualizar backend + frontend
git pull
git submodule update --remote --merge

# Ou atualizar apenas o frontend
cd frontend
git pull origin main
cd ..
```

### ⚙️ Instalar Dependências Frontend

```bash
cd frontend
npm install
cd ..
```

## 🚀 Executar Ambiente de Desenvolvimento

### Opção 1: Docker Compose (Fullstack)

```bash
docker-compose up -d
```

Serviços disponíveis:
- 🌐 Frontend: http://localhost:5173
- 🔧 Backend: http://localhost:8080
- 🗄️ MySQL: localhost:3306

### Opção 2: Manual (Desenvolvimento)

```bash
# Terminal 1 - MySQL
docker-compose up mysql

# Terminal 2 - Backend
./mvnw spring-boot:run

# Terminal 3 - Frontend
cd frontend
npm run dev
```

## 📝 Comandos Úteis

### Verificar Status do Submódulo

```bash
git submodule status
```

### Remover Submódulo (se necessário)

```bash
git submodule deinit frontend
git rm frontend
rm -rf .git/modules/frontend
```

### Re-adicionar Submódulo

```bash
git submodule add https://github.com/growthfolio/react-gamestore-front.git frontend
```

## 🔍 Entendendo .gitmodules

O arquivo `.gitmodules` contém:

```ini
[submodule "frontend"]
    path = frontend
    url = https://github.com/growthfolio/react-gamestore-front.git
```

Isso significa:
- ✅ `frontend/` é um repositório Git separado
- ✅ Commits do frontend não aparecem no backend
- ✅ Backend apenas guarda a referência (commit hash) do frontend
- ✅ Cada repo tem seu próprio histórico

## 🎯 Workflow Recomendado

### Desenvolvendo no Frontend

1. Entre no submódulo: `cd frontend`
2. Crie uma branch: `git checkout -b feature/nova-tela`
3. Faça suas alterações
4. Commit e push: `git push origin feature/nova-tela`
5. Volte ao backend: `cd ..`
6. Atualize a referência: `git add frontend && git commit -m "chore: update frontend"`

### Desenvolvendo no Backend

1. Trabalhe normalmente no código Spring Boot
2. Commits apenas afetam o repositório backend
3. O frontend permanece independente

## ⚠️ Importante

- Nunca commit dentro de `frontend/.git` diretamente pelo backend
- Sempre trabalhe dentro do submódulo para alterações no frontend
- O backend apenas "aponta" para um commit específico do frontend
- Para atualizar, use `git submodule update --remote`

## 🤝 Contribuindo

### Backend (Spring Boot)
Contribuições no repositório principal: `spring-gamestore`

### Frontend (React)
Contribuições no repositório do submódulo: `react-gamestore-front`

Cada repositório tem seu próprio processo de PR e review!
