package com.energygames.lojadegames.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.energygames.lojadegames.enums.StatusJogoEnum;

/**
 * DTO detalhado de produto com mídia estruturada
 * Usado na página de detalhe do produto para máxima experiência
 */
public class ProdutoDetalheResponseDTO {

    private Long id;
    private String nome;
    private String slug;
    private String descricao;
    private String descricaoCompleta;
    private BigDecimal preco;
    private BigDecimal precoComDesconto;
    private BigDecimal desconto;
    private Integer estoque;
    private Boolean emEstoque;
    private Boolean ativo;
    
    // Informações do jogo
    private String plataforma;
    private String desenvolvedor;
    private String publisher;
    private LocalDate dataLancamento;
    private StatusJogoEnum status;
    
    // Ratings
    private BigDecimal ratingIgdb;
    private Double mediaAvaliacoes;
    private Long totalAvaliacoes;
    private Integer totalVotosExternos;
    
    // Categoria
    private CategoriaResumoDTO categoria;
    
    // Plataformas disponíveis
    private List<PlataformaResumoDTO> plataformas;
    
    // Gêneros
    private List<GeneroResumoDTO> generos;
    
    // Mídia estruturada
    private MidiaDTO midia;
    
    // Origem externa (IGDB, etc)
    private OrigemExternaDTO origemExterna;
    
    // Avaliações recentes
    private List<AvaliacaoResponseDTO> avaliacoesRecentes;

    // Inner classes
    public static class CategoriaResumoDTO {
        private Long id;
        private String tipo;
        private String icone;
        private String slug;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getIcone() { return icone; }
        public void setIcone(String icone) { this.icone = icone; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
    }

    public static class PlataformaResumoDTO {
        private Long id;
        private String nome;
        private String abreviacao;
        private String slug;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getAbreviacao() { return abreviacao; }
        public void setAbreviacao(String abreviacao) { this.abreviacao = abreviacao; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
    }

    public static class GeneroResumoDTO {
        private Long id;
        private String nome;
        private String slug;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
    }

    public static class MidiaDTO {
        /**
         * Imagem principal/capa do produto
         */
        private ImagemResponseDTO capa;
        
        /**
         * Todas as imagens (screenshots, artworks, etc) ordenadas
         */
        private List<ImagemResponseDTO> imagens;
        
        /**
         * Apenas screenshots
         */
        private List<ImagemResponseDTO> screenshots;
        
        /**
         * Apenas artworks
         */
        private List<ImagemResponseDTO> artworks;
        
        /**
         * Vídeos do produto (trailers, gameplay, etc)
         */
        private List<VideoResponseDTO> videos;
        
        /**
         * Contagem de mídia disponível
         */
        private MidiaCountDTO contagem;

        public ImagemResponseDTO getCapa() { return capa; }
        public void setCapa(ImagemResponseDTO capa) { this.capa = capa; }
        public List<ImagemResponseDTO> getImagens() { return imagens; }
        public void setImagens(List<ImagemResponseDTO> imagens) { this.imagens = imagens; }
        public List<ImagemResponseDTO> getScreenshots() { return screenshots; }
        public void setScreenshots(List<ImagemResponseDTO> screenshots) { this.screenshots = screenshots; }
        public List<ImagemResponseDTO> getArtworks() { return artworks; }
        public void setArtworks(List<ImagemResponseDTO> artworks) { this.artworks = artworks; }
        public List<VideoResponseDTO> getVideos() { return videos; }
        public void setVideos(List<VideoResponseDTO> videos) { this.videos = videos; }
        public MidiaCountDTO getContagem() { return contagem; }
        public void setContagem(MidiaCountDTO contagem) { this.contagem = contagem; }
    }

    public static class MidiaCountDTO {
        private int totalImagens;
        private int screenshots;
        private int artworks;
        private int videos;

        public int getTotalImagens() { return totalImagens; }
        public void setTotalImagens(int totalImagens) { this.totalImagens = totalImagens; }
        public int getScreenshots() { return screenshots; }
        public void setScreenshots(int screenshots) { this.screenshots = screenshots; }
        public int getArtworks() { return artworks; }
        public void setArtworks(int artworks) { this.artworks = artworks; }
        public int getVideos() { return videos; }
        public void setVideos(int videos) { this.videos = videos; }
    }

    public static class OrigemExternaDTO {
        private String origem; // IGDB, STEAM, etc
        private String idExterno;
        private String urlExterna;

        public String getOrigem() { return origem; }
        public void setOrigem(String origem) { this.origem = origem; }
        public String getIdExterno() { return idExterno; }
        public void setIdExterno(String idExterno) { this.idExterno = idExterno; }
        public String getUrlExterna() { return urlExterna; }
        public void setUrlExterna(String urlExterna) { this.urlExterna = urlExterna; }
    }

    // Getters e Setters do DTO principal
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getDescricaoCompleta() { return descricaoCompleta; }
    public void setDescricaoCompleta(String descricaoCompleta) { this.descricaoCompleta = descricaoCompleta; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public BigDecimal getPrecoComDesconto() { return precoComDesconto; }
    public void setPrecoComDesconto(BigDecimal precoComDesconto) { this.precoComDesconto = precoComDesconto; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    public Boolean getEmEstoque() { return emEstoque; }
    public void setEmEstoque(Boolean emEstoque) { this.emEstoque = emEstoque; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }

    public String getDesenvolvedor() { return desenvolvedor; }
    public void setDesenvolvedor(String desenvolvedor) { this.desenvolvedor = desenvolvedor; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }

    public StatusJogoEnum getStatus() { return status; }
    public void setStatus(StatusJogoEnum status) { this.status = status; }

    public BigDecimal getRatingIgdb() { return ratingIgdb; }
    public void setRatingIgdb(BigDecimal ratingIgdb) { this.ratingIgdb = ratingIgdb; }

    public Double getMediaAvaliacoes() { return mediaAvaliacoes; }
    public void setMediaAvaliacoes(Double mediaAvaliacoes) { this.mediaAvaliacoes = mediaAvaliacoes; }

    public Long getTotalAvaliacoes() { return totalAvaliacoes; }
    public void setTotalAvaliacoes(Long totalAvaliacoes) { this.totalAvaliacoes = totalAvaliacoes; }

    public Integer getTotalVotosExternos() { return totalVotosExternos; }
    public void setTotalVotosExternos(Integer totalVotosExternos) { this.totalVotosExternos = totalVotosExternos; }

    public CategoriaResumoDTO getCategoria() { return categoria; }
    public void setCategoria(CategoriaResumoDTO categoria) { this.categoria = categoria; }

    public List<PlataformaResumoDTO> getPlataformas() { return plataformas; }
    public void setPlataformas(List<PlataformaResumoDTO> plataformas) { this.plataformas = plataformas; }

    public List<GeneroResumoDTO> getGeneros() { return generos; }
    public void setGeneros(List<GeneroResumoDTO> generos) { this.generos = generos; }

    public MidiaDTO getMidia() { return midia; }
    public void setMidia(MidiaDTO midia) { this.midia = midia; }

    public OrigemExternaDTO getOrigemExterna() { return origemExterna; }
    public void setOrigemExterna(OrigemExternaDTO origemExterna) { this.origemExterna = origemExterna; }

    public List<AvaliacaoResponseDTO> getAvaliacoesRecentes() { return avaliacoesRecentes; }
    public void setAvaliacoesRecentes(List<AvaliacaoResponseDTO> avaliacoesRecentes) { this.avaliacoesRecentes = avaliacoesRecentes; }
}
