package com.energygames.lojadegames.enums;

public enum ConquistaEnum {
    PRIMEIRA_COMPRA("Primeira Compra", "Realizou sua primeira compra", "🛒", "compras", 1),
    COLECIONADOR("Colecionador", "Comprou 10 jogos diferentes", "🎮", "compras", 10),
    MEGA_COLECIONADOR("Mega Colecionador", "Comprou 50 jogos diferentes", "🏆", "compras", 50),
    CRITICO("Crítico", "Fez sua primeira avaliação", "⭐", "avaliacoes", 1),
    AVALIADOR_EXPERT("Avaliador Expert", "Fez 10 avaliações", "📝", "avaliacoes", 10),
    CURADOR("Curador", "Fez 25 avaliações", "🎯", "avaliacoes", 25),
    LISTA_DESEJOS("Lista de Desejos", "Adicionou 5 jogos aos favoritos", "❤️", "favoritos", 5),
    WISHLIST_MASTER("Wishlist Master", "Adicionou 20 jogos aos favoritos", "💎", "favoritos", 20),
    VETERANO("Veterano", "Membro há mais de 6 meses", "🏅", "tempo", 180),
    LENDA("Lenda", "Membro há mais de 1 ano", "👑", "tempo", 365);

    private final String nome;
    private final String descricao;
    private final String icone;
    private final String tipo;
    private final int meta;

    ConquistaEnum(String nome, String descricao, String icone, String tipo, int meta) {
        this.nome = nome;
        this.descricao = descricao;
        this.icone = icone;
        this.tipo = tipo;
        this.meta = meta;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getIcone() { return icone; }
    public String getTipo() { return tipo; }
    public int getMeta() { return meta; }
}
