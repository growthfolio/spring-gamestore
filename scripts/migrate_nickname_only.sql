-- Script de migração mínimo para nickname
-- Apenas migra campo 'nome' para 'nickname' se necessário

-- Verificar estrutura atual da tabela
DESCRIBE tb_usuarios;

-- Migrar dados de nome para nickname se a coluna nome existir
UPDATE tb_usuarios 
SET nickname = nome 
WHERE nome IS NOT NULL AND (nickname IS NULL OR nickname = '');

-- Verificação final
SELECT COUNT(*) as total_usuarios FROM tb_usuarios;
