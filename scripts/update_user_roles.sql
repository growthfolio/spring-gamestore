-- Script para gerenciar roles de usuários
-- Uso: Execute os comandos conforme necessário

-- 1. Consultar usuário por email
SELECT id, nome, usuario FROM tb_usuarios WHERE usuario = 'felipealexandrej@outlook.com.br';

-- 2. Verificar roles atuais do usuário (substitua o ID)
SELECT ur.usuario_id, u.nome, u.usuario, ur.role 
FROM usuario_roles ur 
JOIN tb_usuarios u ON ur.usuario_id = u.id 
WHERE u.usuario = 'felipealexandrej@outlook.com.br';

-- 3. Adicionar role ADMIN (substitua o usuario_id pelo ID correto)
INSERT INTO usuario_roles (usuario_id, role) VALUES (3, 'ROLE_ADMIN');

-- 4. Remover role específica (se necessário)
-- DELETE FROM usuario_roles WHERE usuario_id = 3 AND role = 'ROLE_USER';

-- 5. Consultar todos os usuários com suas roles
SELECT u.id, u.nome, u.usuario, ur.role
FROM tb_usuarios u
LEFT JOIN usuario_roles ur ON u.id = ur.usuario_id
ORDER BY u.id, ur.role;

-- 6. Consultar apenas usuários ADMIN
SELECT DISTINCT u.id, u.nome, u.usuario
FROM tb_usuarios u
JOIN usuario_roles ur ON u.id = ur.usuario_id
WHERE ur.role = 'ROLE_ADMIN';
