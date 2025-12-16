package com.energygames.lojadegames.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.energygames.lojadegames.enums.NivelUsuarioEnum;

public class PerfilStatsDTO {
    private Long usuarioId;
    private String nickname;
    private String email;
    private String foto;
    private LocalDateTime membroDesde;
    
    // Estatísticas
    private int totalCompras;
    private int totalAvaliacoes;
    private int totalFavoritos;
    private int totalItensCarrinho;
    private BigDecimal totalGasto;
    
    // Nível e Progresso
    private NivelUsuarioEnum nivel;
    private NivelUsuarioEnum proximoNivel;
    private int comprasParaProximoNivel;
    private int progressoNivel; // 0-100%
    
    // Conquistas
    private List<ConquistaDTO> conquistas;
    private int totalConquistas;
    
    // Gêneros favoritos (baseado em compras)
    private List<GeneroFavoritoDTO> generosFavoritos;
    
    // Últimas compras
    private List<CompraResumoDTO> ultimasCompras;

    // Getters e Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    
    public LocalDateTime getMembroDesde() { return membroDesde; }
    public void setMembroDesde(LocalDateTime membroDesde) { this.membroDesde = membroDesde; }
    
    public int getTotalCompras() { return totalCompras; }
    public void setTotalCompras(int totalCompras) { this.totalCompras = totalCompras; }
    
    public int getTotalAvaliacoes() { return totalAvaliacoes; }
    public void setTotalAvaliacoes(int totalAvaliacoes) { this.totalAvaliacoes = totalAvaliacoes; }
    
    public int getTotalFavoritos() { return totalFavoritos; }
    public void setTotalFavoritos(int totalFavoritos) { this.totalFavoritos = totalFavoritos; }
    
    public int getTotalItensCarrinho() { return totalItensCarrinho; }
    public void setTotalItensCarrinho(int totalItensCarrinho) { this.totalItensCarrinho = totalItensCarrinho; }
    
    public BigDecimal getTotalGasto() { return totalGasto; }
    public void setTotalGasto(BigDecimal totalGasto) { this.totalGasto = totalGasto; }
    
    public NivelUsuarioEnum getNivel() { return nivel; }
    public void setNivel(NivelUsuarioEnum nivel) { this.nivel = nivel; }
    
    public NivelUsuarioEnum getProximoNivel() { return proximoNivel; }
    public void setProximoNivel(NivelUsuarioEnum proximoNivel) { this.proximoNivel = proximoNivel; }
    
    public int getComprasParaProximoNivel() { return comprasParaProximoNivel; }
    public void setComprasParaProximoNivel(int comprasParaProximoNivel) { this.comprasParaProximoNivel = comprasParaProximoNivel; }
    
    public int getProgressoNivel() { return progressoNivel; }
    public void setProgressoNivel(int progressoNivel) { this.progressoNivel = progressoNivel; }
    
    public List<ConquistaDTO> getConquistas() { return conquistas; }
    public void setConquistas(List<ConquistaDTO> conquistas) { this.conquistas = conquistas; }
    
    public int getTotalConquistas() { return totalConquistas; }
    public void setTotalConquistas(int totalConquistas) { this.totalConquistas = totalConquistas; }
    
    public List<GeneroFavoritoDTO> getGenerosFavoritos() { return generosFavoritos; }
    public void setGenerosFavoritos(List<GeneroFavoritoDTO> generosFavoritos) { this.generosFavoritos = generosFavoritos; }
    
    public List<CompraResumoDTO> getUltimasCompras() { return ultimasCompras; }
    public void setUltimasCompras(List<CompraResumoDTO> ultimasCompras) { this.ultimasCompras = ultimasCompras; }

    // DTOs internos
    public static class ConquistaDTO {
        private String codigo;
        private String nome;
        private String descricao;
        private String icone;
        private boolean desbloqueada;
        private int progresso;
        private int meta;

        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public String getIcone() { return icone; }
        public void setIcone(String icone) { this.icone = icone; }
        public boolean isDesbloqueada() { return desbloqueada; }
        public void setDesbloqueada(boolean desbloqueada) { this.desbloqueada = desbloqueada; }
        public int getProgresso() { return progresso; }
        public void setProgresso(int progresso) { this.progresso = progresso; }
        public int getMeta() { return meta; }
        public void setMeta(int meta) { this.meta = meta; }
    }

    public static class GeneroFavoritoDTO {
        private String nome;
        private int quantidade;
        private int percentual;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
        public int getPercentual() { return percentual; }
        public void setPercentual(int percentual) { this.percentual = percentual; }
    }

    public static class CompraResumoDTO {
        private Long pedidoId;
        private String produtoNome;
        private String produtoImagem;
        private LocalDateTime data;
        private BigDecimal valor;

        public Long getPedidoId() { return pedidoId; }
        public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
        public String getProdutoNome() { return produtoNome; }
        public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }
        public String getProdutoImagem() { return produtoImagem; }
        public void setProdutoImagem(String produtoImagem) { this.produtoImagem = produtoImagem; }
        public LocalDateTime getData() { return data; }
        public void setData(LocalDateTime data) { this.data = data; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }
    }
}
