#!/bin/bash

# URL da API
API_URL="http://localhost:8080"

# Credenciais do Admin
ADMIN_EMAIL="admin@energygames.com"
ADMIN_PASS="admin123"

echo "=== Teste de Integração IGDB ==="
echo "Certifique-se que a aplicação está rodando!"
echo ""

echo "1. Autenticando como Admin..."
RESPONSE=$(curl -s -X POST "$API_URL/usuarios/logar" \
  -H "Content-Type: application/json" \
  -d "{\"usuario\": \"$ADMIN_EMAIL\", \"senha\": \"$ADMIN_PASS\"}")

# Extrai o token de forma simples (assumindo que o json é simples)
TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
  echo "Erro ao autenticar. Verifique se o usuário admin foi criado e a aplicação está rodando."
  echo "Resposta: $RESPONSE"
  exit 1
fi

echo "Token obtido com sucesso!"
echo ""

echo "2. Testando busca na IGDB (Jogos Populares)..."
SEARCH_RESPONSE=$(curl -v -S -X GET "$API_URL/admin/igdb/search?limit=5" \
  -H "Authorization: Bearer $TOKEN")

# Tenta formatar com python3 se disponível, senão imprime raw
echo "Raw Response:"
echo "$SEARCH_RESPONSE"
if command -v python3 &> /dev/null; then
    if [ ! -z "$SEARCH_RESPONSE" ]; then
        echo $SEARCH_RESPONSE | python3 -m json.tool
    fi
else
    echo $SEARCH_RESPONSE
fi

echo ""
echo "3. Testando busca por nome (Mario)..."
SEARCH_RESPONSE_MARIO=$(curl -v -S -X GET "$API_URL/admin/igdb/search?nome=Mario&limit=3" \
  -H "Authorization: Bearer $TOKEN")

echo "Raw Response Mario:"
echo "$SEARCH_RESPONSE_MARIO"
if command -v python3 &> /dev/null; then
    if [ ! -z "$SEARCH_RESPONSE_MARIO" ]; then
        echo $SEARCH_RESPONSE_MARIO | python3 -m json.tool
    fi
else
    echo $SEARCH_RESPONSE_MARIO
fi
