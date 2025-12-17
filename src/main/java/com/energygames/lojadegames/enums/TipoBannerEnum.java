package com.energygames.lojadegames.enums;

/**
 * Tipo do banner no carousel da home page.
 */
public enum TipoBannerEnum {
    /**
     * Banner vinculado a um produto específico.
     * Usa imagens do produto (artwork, screenshot) e linka para a página do produto.
     */
    PRODUTO,
    
    /**
     * Banner com imagem e link customizados.
     * Pode ser usado para promoções, eventos ou links externos.
     */
    CUSTOM,
    
    /**
     * Banner de categoria/gênero.
     * Linka para a listagem filtrada por categoria.
     */
    CATEGORIA
}
