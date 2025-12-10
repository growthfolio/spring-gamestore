package com.energygames.lojadegames.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * DTO de resposta para status de importação de jogos da IGDB
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IgdbImportStatusDTO {

    private boolean sucesso;
    private String mensagem;
    private Long produtoId;
    private String nomeProduto;
    private Long igdbId;
    private LocalDateTime dataImportacao;
    private String erro;

    // Construtores

    public IgdbImportStatusDTO() {}

    public IgdbImportStatusDTO(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    // Métodos estáticos para facilitar criação

    public static IgdbImportStatusDTO sucesso(Long produtoId, String nomeProduto, Long igdbId) {
        IgdbImportStatusDTO dto = new IgdbImportStatusDTO();
        dto.setSucesso(true);
        dto.setMensagem("Jogo importado com sucesso");
        dto.setProdutoId(produtoId);
        dto.setNomeProduto(nomeProduto);
        dto.setIgdbId(igdbId);
        dto.setDataImportacao(LocalDateTime.now());
        return dto;
    }

    public static IgdbImportStatusDTO jaImportado(Long produtoId, String nomeProduto) {
        IgdbImportStatusDTO dto = new IgdbImportStatusDTO();
        dto.setSucesso(true);
        dto.setMensagem("Jogo já estava importado");
        dto.setProdutoId(produtoId);
        dto.setNomeProduto(nomeProduto);
        return dto;
    }

    public static IgdbImportStatusDTO erro(String mensagemErro) {
        IgdbImportStatusDTO dto = new IgdbImportStatusDTO();
        dto.setSucesso(false);
        dto.setMensagem("Erro na importação");
        dto.setErro(mensagemErro);
        return dto;
    }

    // Getters e Setters

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Long getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Long igdbId) {
        this.igdbId = igdbId;
    }

    public LocalDateTime getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(LocalDateTime dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }
}
