package com.energygames.lojadegames.service.impl;

import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlugGeneratorService {

    private final ProdutoRepository produtoRepository;

    /**
     * Gera slugs para todos os produtos que não possuem
     */
    @Transactional
    public void gerarSlugsParaProdutosSemSlug() {
        log.info("Iniciando geração de slugs para produtos sem slug");
        
        List<Produto> produtosSemSlug = produtoRepository.findAll().stream()
                .filter(produto -> produto.getSlug() == null || produto.getSlug().trim().isEmpty())
                .toList();
        
        log.info("Encontrados {} produtos sem slug", produtosSemSlug.size());
        
        for (Produto produto : produtosSemSlug) {
            String novoSlug = gerarSlugUnico(produto.getNome(), produto.getId());
            produto.setSlug(novoSlug);
            produtoRepository.save(produto);
            log.debug("Slug gerado para produto {}: {}", produto.getNome(), novoSlug);
        }
        
        log.info("Geração de slugs concluída");
    }

    /**
     * Gera um slug único baseado no nome do produto
     */
    public String gerarSlugUnico(String nome, Long produtoId) {
        String slugBase = gerarSlug(nome);
        String slugFinal = slugBase;
        int contador = 1;
        
        // Verifica se já existe um produto com este slug (exceto o próprio produto)
        while (produtoRepository.findBySlug(slugFinal)
                .filter(produto -> !produto.getId().equals(produtoId))
                .isPresent()) {
            slugFinal = slugBase + "-" + contador;
            contador++;
        }
        
        return slugFinal;
    }

    /**
     * Gera um slug a partir de um texto
     */
    private String gerarSlug(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "produto-sem-nome";
        }
        
        // Normalizar caracteres acentuados
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        
        // Converter para minúsculas e remover caracteres especiais
        return normalizado.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "")
                .trim();
    }

    /**
     * Verifica e corrige slugs duplicados
     */
    @Transactional
    public void corrigirSlugsDuplicados() {
        log.info("Verificando slugs duplicados");
        
        List<String> slugsDuplicados = produtoRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Produto::getSlug,
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(java.util.Map.Entry::getKey)
                .toList();
        
        for (String slugDuplicado : slugsDuplicados) {
            List<Produto> produtosComSlugDuplicado = produtoRepository.findAll().stream()
                    .filter(produto -> slugDuplicado.equals(produto.getSlug()))
                    .toList();
            
            // Manter o primeiro produto com o slug original
            for (int i = 1; i < produtosComSlugDuplicado.size(); i++) {
                Produto produto = produtosComSlugDuplicado.get(i);
                String novoSlug = gerarSlugUnico(produto.getNome(), produto.getId());
                produto.setSlug(novoSlug);
                produtoRepository.save(produto);
                log.info("Slug duplicado corrigido: {} -> {}", slugDuplicado, novoSlug);
            }
        }
    }
}
