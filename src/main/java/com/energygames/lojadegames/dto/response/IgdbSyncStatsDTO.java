package com.energygames.lojadegames.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * DTO de resposta para estatísticas de sincronização IGDB
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IgdbSyncStatsDTO {

    private Integer totalProdutos;
    private Integer produtosIgdb;
    private Integer produtosAtivos;
    private Integer produtosDesatualizados;
    private Integer produtosSincronizados;
    private Integer erros;
    private LocalDateTime ultimaSincronizacao;
    private LocalDateTime proximaSincronizacao;
    private boolean apiDisponivel;
    private String statusApi;

    // Getters e Setters

    public Integer getTotalProdutos() {
        return totalProdutos;
    }

    public void setTotalProdutos(Integer totalProdutos) {
        this.totalProdutos = totalProdutos;
    }

    public Integer getProdutosIgdb() {
        return produtosIgdb;
    }

    public void setProdutosIgdb(Integer produtosIgdb) {
        this.produtosIgdb = produtosIgdb;
    }

    public Integer getProdutosAtivos() {
        return produtosAtivos;
    }

    public void setProdutosAtivos(Integer produtosAtivos) {
        this.produtosAtivos = produtosAtivos;
    }

    public Integer getProdutosDesatualizados() {
        return produtosDesatualizados;
    }

    public void setProdutosDesatualizados(Integer produtosDesatualizados) {
        this.produtosDesatualizados = produtosDesatualizados;
    }

    public Integer getProdutosSincronizados() {
        return produtosSincronizados;
    }

    public void setProdutosSincronizados(Integer produtosSincronizados) {
        this.produtosSincronizados = produtosSincronizados;
    }

    public Integer getErros() {
        return erros;
    }

    public void setErros(Integer erros) {
        this.erros = erros;
    }

    public LocalDateTime getUltimaSincronizacao() {
        return ultimaSincronizacao;
    }

    public void setUltimaSincronizacao(LocalDateTime ultimaSincronizacao) {
        this.ultimaSincronizacao = ultimaSincronizacao;
    }

    public LocalDateTime getProximaSincronizacao() {
        return proximaSincronizacao;
    }

    public void setProximaSincronizacao(LocalDateTime proximaSincronizacao) {
        this.proximaSincronizacao = proximaSincronizacao;
    }

    public boolean isApiDisponivel() {
        return apiDisponivel;
    }

    public void setApiDisponivel(boolean apiDisponivel) {
        this.apiDisponivel = apiDisponivel;
    }

    public String getStatusApi() {
        return statusApi;
    }

    public void setStatusApi(String statusApi) {
        this.statusApi = statusApi;
    }
}
