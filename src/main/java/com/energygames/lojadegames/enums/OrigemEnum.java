package com.energygames.lojadegames.enums;

/**
 * Enum que representa a origem dos dados de um produto.
 * Permite rastrear se o produto foi cadastrado manualmente ou importado de APIs externas.
 */
public enum OrigemEnum {
    /**
     * Produto cadastrado manualmente por um administrador
     */
    MANUAL,
    
    /**
     * Produto importado da API IGDB (Internet Game Database)
     */
    IGDB,
    
    /**
     * Produto importado da API RAWG (futuro)
     */
    RAWG,
    
    /**
     * Dados de preço importados da API CheapShark (futuro)
     */
    CHEAPSHARK
}
