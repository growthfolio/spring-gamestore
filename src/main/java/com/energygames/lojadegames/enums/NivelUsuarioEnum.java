package com.energygames.lojadegames.enums;

public enum NivelUsuarioEnum {
    BRONZE(0, 0, "Bronze", "Iniciante na jornada gamer"),
    PRATA(1, 3, "Prata", "Jogador dedicado"),
    OURO(2, 10, "Ouro", "Veterano dos games"),
    PLATINA(3, 25, "Platina", "Mestre dos jogos"),
    DIAMANTE(4, 50, "Diamante", "Lenda gamer");

    private final int ordem;
    private final int comprasMinimas;
    private final String nome;
    private final String descricao;

    NivelUsuarioEnum(int ordem, int comprasMinimas, String nome, String descricao) {
        this.ordem = ordem;
        this.comprasMinimas = comprasMinimas;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getOrdem() { return ordem; }
    public int getComprasMinimas() { return comprasMinimas; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }

    public static NivelUsuarioEnum calcularNivel(int totalCompras) {
        NivelUsuarioEnum nivel = BRONZE;
        for (NivelUsuarioEnum n : values()) {
            if (totalCompras >= n.comprasMinimas) {
                nivel = n;
            }
        }
        return nivel;
    }

    public NivelUsuarioEnum getProximoNivel() {
        for (NivelUsuarioEnum n : values()) {
            if (n.ordem == this.ordem + 1) return n;
        }
        return this;
    }
}
