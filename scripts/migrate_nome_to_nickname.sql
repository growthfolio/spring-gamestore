-- Script de migração: nome -> nickname na tabela tb_usuarios
-- Execute este script ANTES de iniciar a aplicação com as novas alterações

-- 1. Adicionar coluna nickname (se não existir)
ALTER TABLE tb_usuarios ADD COLUMN IF NOT EXISTS nickname VARCHAR(255);

-- 2. Copiar dados de nome para nickname
UPDATE tb_usuarios SET nickname = nome WHERE nickname IS NULL;

-- 3. Tornar nickname NOT NULL (após migração dos dados)
ALTER TABLE tb_usuarios MODIFY COLUMN nickname VARCHAR(255) NOT NULL;

-- 4. (OPCIONAL) Remover coluna nome antiga após confirmar que tudo funciona
-- ALTER TABLE tb_usuarios DROP COLUMN nome;
