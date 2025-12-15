-- Script de migração completa para tabela tb_usuarios
-- Migra campo 'nome' para 'nickname' e garante integridade dos dados

-- 1. Verificar se a coluna nickname existe, se não, criar
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.columns 
WHERE table_schema = DATABASE() 
AND table_name = 'tb_usuarios' 
AND column_name = 'nickname';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE tb_usuarios ADD COLUMN nickname VARCHAR(255)', 
    'SELECT "Coluna nickname já existe"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. Migrar dados de nome para nickname (apenas onde nickname está vazio)
UPDATE tb_usuarios 
SET nickname = COALESCE(nome, CONCAT('user_', id)) 
WHERE nickname IS NULL OR nickname = '';

-- 3. Garantir que todos os usuários tenham nickname único
UPDATE tb_usuarios u1 
SET nickname = CONCAT(nickname, '_', id) 
WHERE EXISTS (
    SELECT 1 FROM tb_usuarios u2 
    WHERE u2.nickname = u1.nickname AND u2.id != u1.id
);

-- 4. Tornar nickname NOT NULL se ainda não for
SET @sql = 'ALTER TABLE tb_usuarios MODIFY COLUMN nickname VARCHAR(255) NOT NULL';
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. Verificar e corrigir emails inválidos (opcional)
UPDATE tb_usuarios 
SET email = CONCAT('user_', id, '@temp.com') 
WHERE email IS NULL OR email = '' OR email NOT LIKE '%@%';

-- 6. Garantir que emails sejam únicos
UPDATE tb_usuarios u1 
SET email = CONCAT(SUBSTRING_INDEX(email, '@', 1), '_', id, '@', SUBSTRING_INDEX(email, '@', -1))
WHERE EXISTS (
    SELECT 1 FROM tb_usuarios u2 
    WHERE u2.email = u1.email AND u2.id != u1.id
);

-- Verificação final
SELECT 
    COUNT(*) as total_usuarios,
    COUNT(DISTINCT nickname) as nicknames_unicos,
    COUNT(DISTINCT email) as emails_unicos
FROM tb_usuarios;
