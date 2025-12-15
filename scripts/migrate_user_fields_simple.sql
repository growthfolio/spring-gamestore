-- Script de migração simples para tabela tb_usuarios
-- Migra campo 'nome' para 'nickname'

-- 1. Verificar se a coluna nickname existe, se não, criar
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.columns 
WHERE table_schema = DATABASE() 
AND table_name = 'tb_usuarios' 
AND column_name = 'nickname';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE tb_usuarios ADD COLUMN nickname VARCHAR(255)', 
    'SELECT "Coluna nickname já existe" as status');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. Migrar dados de nome para nickname onde nickname está vazio
UPDATE tb_usuarios 
SET nickname = COALESCE(nome, CONCAT('user_', id)) 
WHERE nickname IS NULL OR nickname = '';

-- 3. Tornar nickname NOT NULL
ALTER TABLE tb_usuarios MODIFY COLUMN nickname VARCHAR(255) NOT NULL;

-- Verificação final
SELECT 
    COUNT(*) as total_usuarios,
    COUNT(DISTINCT nickname) as nicknames_unicos,
    COUNT(DISTINCT email) as emails_unicos
FROM tb_usuarios;
