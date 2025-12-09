package com.energygames.lojadegames.model;

import java.time.LocalDateTime;

import com.energygames.lojadegames.enums.OrigemEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entidade que rastreia a origem e sincronização de produtos importados de APIs externas.
 * Relacionamento 1:1 com Produto.
 */
@Entity
@Table(name = "tb_produto_origem_externa")
public class ProdutoOrigemExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    @NotNull(message = "Origem é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrigemEnum origem; // IGDB, MANUAL, RAWG, etc.

    @Column(name = "id_externo", length = 100)
    private String idExterno; // ID na API externa (ex: game ID da IGDB)

    @Column(name = "url_externa", length = 500)
    private String urlExterna; // URL no site da API externa

    @Column(name = "data_importacao")
    private LocalDateTime dataImportacao;

    @Column(name = "data_ultima_sincronizacao")
    private LocalDateTime dataUltimaSincronizacao;

    @Column(name = "versao_dados")
    private Integer versaoDados; // Para controle de versão dos dados importados

    @NotNull
    @Column(name = "sincronizacao_ativa", columnDefinition = "boolean default true")
    private Boolean sincronizacaoAtiva = true; // Se deve atualizar periodicamente

    @PrePersist
    protected void onCreate() {
        if (dataImportacao == null) {
            dataImportacao = LocalDateTime.now();
        }
        if (versaoDados == null) {
            versaoDados = 1;
        }
    }

    // Construtores
    public ProdutoOrigemExterna() {
    }

    public ProdutoOrigemExterna(Produto produto, OrigemEnum origem, String idExterno) {
        this.produto = produto;
        this.origem = origem;
        this.idExterno = idExterno;
        this.sincronizacaoAtiva = true;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public OrigemEnum getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemEnum origem) {
        this.origem = origem;
    }

    public String getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(String idExterno) {
        this.idExterno = idExterno;
    }

    public String getUrlExterna() {
        return urlExterna;
    }

    public void setUrlExterna(String urlExterna) {
        this.urlExterna = urlExterna;
    }

    public LocalDateTime getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(LocalDateTime dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public LocalDateTime getDataUltimaSincronizacao() {
        return dataUltimaSincronizacao;
    }

    public void setDataUltimaSincronizacao(LocalDateTime dataUltimaSincronizacao) {
        this.dataUltimaSincronizacao = dataUltimaSincronizacao;
    }

    public Integer getVersaoDados() {
        return versaoDados;
    }

    public void setVersaoDados(Integer versaoDados) {
        this.versaoDados = versaoDados;
    }

    public Boolean getSincronizacaoAtiva() {
        return sincronizacaoAtiva;
    }

    public void setSincronizacaoAtiva(Boolean sincronizacaoAtiva) {
        this.sincronizacaoAtiva = sincronizacaoAtiva;
    }

    /**
     * Atualiza a data de sincronização e incrementa a versão
     */
    public void registrarSincronizacao() {
        this.dataUltimaSincronizacao = LocalDateTime.now();
        if (this.versaoDados != null) {
            this.versaoDados++;
        }
    }
}
