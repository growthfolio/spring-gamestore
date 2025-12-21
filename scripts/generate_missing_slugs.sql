-- Script para gerar slugs para produtos que não possuem
-- Este script deve ser executado no banco de dados

-- Função para gerar slug (simulação em SQL)
UPDATE tb_produtos 
SET slug = LOWER(
    REPLACE(
        REPLACE(
            REPLACE(
                REPLACE(
                    REPLACE(
                        REPLACE(
                            REPLACE(
                                REPLACE(
                                    REPLACE(
                                        REPLACE(nome, 'ã', 'a'),
                                        'á', 'a'
                                    ),
                                    'à', 'a'
                                ),
                                'â', 'a'
                            ),
                            'é', 'e'
                        ),
                        'ê', 'e'
                    ),
                    'í', 'i'
                ),
                'ó', 'o'
            ),
            'ô', 'o'
        ),
        'ú', 'u'
    )
)
WHERE slug IS NULL OR slug = '';

-- Remover caracteres especiais e espaços
UPDATE tb_produtos 
SET slug = REGEXP_REPLACE(
    REGEXP_REPLACE(slug, '[^a-z0-9\\s-]', '', 'g'),
    '\\s+', '-', 'g'
)
WHERE slug IS NOT NULL;

-- Remover hífens duplos e limpar
UPDATE tb_produtos 
SET slug = REGEXP_REPLACE(
    TRIM(BOTH '-' FROM slug),
    '-+', '-', 'g'
)
WHERE slug IS NOT NULL;

-- Verificar produtos sem slug
SELECT id, nome, slug 
FROM tb_produtos 
WHERE slug IS NULL OR slug = '' OR LENGTH(slug) < 3;

-- Verificar duplicatas de slug
SELECT slug, COUNT(*) as count
FROM tb_produtos 
WHERE slug IS NOT NULL 
GROUP BY slug 
HAVING COUNT(*) > 1;
