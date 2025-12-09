package com.energygames.lojadegames.enums;

/**
 * Enum que representa o status atual de um jogo.
 * Baseado nos status da IGDB API.
 */
public enum StatusJogoEnum {
    /**
     * Jogo já foi lançado e está disponível
     */
    RELEASED,
    
    /**
     * Jogo em acesso antecipado (Early Access)
     */
    EARLY_ACCESS,
    
    /**
     * Jogo em fase beta
     */
    BETA,
    
    /**
     * Jogo em fase alpha
     */
    ALPHA,
    
    /**
     * Jogo ainda não foi lançado (Coming Soon)
     */
    UPCOMING,
    
    /**
     * Jogo foi cancelado
     */
    CANCELLED,
    
    /**
     * Jogo descontinuado ou removido de circulação
     */
    DISCONTINUED
}
