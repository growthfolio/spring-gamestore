#!/bin/bash

# Script para iniciar ambiente de desenvolvimento fullstack
# Backend (Spring Boot) + Frontend (React/Vite)

set -e

echo "🚀 Iniciando ambiente de desenvolvimento Spring GameStore..."
echo ""

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Diretório do projeto
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$PROJECT_DIR/frontend"

# Arquivo para armazenar PIDs
PID_FILE="$PROJECT_DIR/.dev-pids"

# Função para limpar processos ao sair
cleanup() {
    echo ""
    echo "${YELLOW}🛑 Encerrando serviços...${NC}"
    
    if [ -f "$PID_FILE" ]; then
        while IFS= read -r pid; do
            if ps -p "$pid" > /dev/null 2>&1; then
                echo "  Matando processo $pid"
                kill "$pid" 2>/dev/null || true
            fi
        done < "$PID_FILE"
        rm -f "$PID_FILE"
    fi
    
    echo "${GREEN}✅ Serviços encerrados${NC}"
    exit 0
}

# Capturar Ctrl+C e outros sinais
trap cleanup SIGINT SIGTERM EXIT

# Verificar se frontend existe
if [ ! -d "$FRONTEND_DIR" ]; then
    echo "${YELLOW}⚠️  Frontend não encontrado em $FRONTEND_DIR${NC}"
    echo "${YELLOW}   Execute: git submodule update --init --recursive${NC}"
    exit 1
fi

# Instalar dependências do frontend se necessário
if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
    echo "${BLUE}📦 Instalando dependências do frontend...${NC}"
    cd "$FRONTEND_DIR"
    npm install
    cd "$PROJECT_DIR"
fi

# Limpar PID file anterior
> "$PID_FILE"

echo "${BLUE}🔧 Iniciando backend (Spring Boot)...${NC}"
./mvnw spring-boot:run > logs/backend.log 2>&1 &
BACKEND_PID=$!
echo "$BACKEND_PID" >> "$PID_FILE"
echo "${GREEN}   Backend iniciado (PID: $BACKEND_PID)${NC}"
echo "   Logs: tail -f logs/backend.log"

# Aguardar backend iniciar (verificar health)
echo "${BLUE}⏳ Aguardando backend ficar online...${NC}"
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "${GREEN}   Backend está online!${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "${YELLOW}   ⚠️  Backend demorou mais que o esperado${NC}"
        echo "   Verifique os logs: tail -f logs/backend.log"
    fi
    sleep 2
done

echo ""
echo "${BLUE}⚛️  Iniciando frontend (React + Vite)...${NC}"
cd "$FRONTEND_DIR"
npm run dev > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "$FRONTEND_PID" >> "$PID_FILE"
echo "${GREEN}   Frontend iniciado (PID: $FRONTEND_PID)${NC}"
echo "   Logs: tail -f logs/frontend.log"
cd "$PROJECT_DIR"

echo ""
echo "${GREEN}✅ Ambiente de desenvolvimento iniciado!${NC}"
echo ""
echo "📍 URLs:"
echo "   Frontend: ${BLUE}http://localhost:5173${NC}"
echo "   Backend:  ${BLUE}http://localhost:8080${NC}"
echo "   Swagger:  ${BLUE}http://localhost:8080/swagger-ui/index.html${NC}"
echo ""
echo "📝 Logs:"
echo "   Backend:  tail -f logs/backend.log"
echo "   Frontend: tail -f logs/frontend.log"
echo ""
echo "${YELLOW}Pressione Ctrl+C para encerrar todos os serviços${NC}"
echo ""

# Manter script rodando
wait
