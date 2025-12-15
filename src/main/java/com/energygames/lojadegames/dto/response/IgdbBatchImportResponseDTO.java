package com.energygames.lojadegames.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de resposta para importação em lote de jogos da IGDB
 * Fornece feedback visual detalhado sobre o progresso e resultados
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IgdbBatchImportResponseDTO {

    private int totalSolicitado;
    private int totalProcessado;
    private int sucessos;
    private int jaImportados;
    private int falhas;
    private LocalDateTime dataExecucao;
    private String status; // "CONCLUIDO", "PARCIAL", "ERRO"
    private List<IgdbImportStatusDTO> resultados;
    private String mensagemResumo;

    public IgdbBatchImportResponseDTO() {
        this.resultados = new ArrayList<>();
        this.dataExecucao = LocalDateTime.now();
    }

    // Método auxiliar para calcular status
    public void calcularStatus() {
        if (falhas == 0) {
            this.status = "CONCLUIDO";
        } else if (sucessos > 0 || jaImportados > 0) {
            this.status = "PARCIAL";
        } else {
            this.status = "ERRO";
        }
        
        this.mensagemResumo = String.format(
            "Processados %d de %d jogos: %d importados com sucesso, %d já existentes, %d falhas",
            totalProcessado, totalSolicitado, sucessos, jaImportados, falhas
        );
    }

    // Getters e Setters

    public int getTotalSolicitado() {
        return totalSolicitado;
    }

    public void setTotalSolicitado(int totalSolicitado) {
        this.totalSolicitado = totalSolicitado;
    }

    public int getTotalProcessado() {
        return totalProcessado;
    }

    public void setTotalProcessado(int totalProcessado) {
        this.totalProcessado = totalProcessado;
    }

    public int getSucessos() {
        return sucessos;
    }

    public void setSucessos(int sucessos) {
        this.sucessos = sucessos;
    }

    public int getJaImportados() {
        return jaImportados;
    }

    public void setJaImportados(int jaImportados) {
        this.jaImportados = jaImportados;
    }

    public int getFalhas() {
        return falhas;
    }

    public void setFalhas(int falhas) {
        this.falhas = falhas;
    }

    public LocalDateTime getDataExecucao() {
        return dataExecucao;
    }

    public void setDataExecucao(LocalDateTime dataExecucao) {
        this.dataExecucao = dataExecucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<IgdbImportStatusDTO> getResultados() {
        return resultados;
    }

    public void setResultados(List<IgdbImportStatusDTO> resultados) {
        this.resultados = resultados;
    }

    public String getMensagemResumo() {
        return mensagemResumo;
    }

    public void setMensagemResumo(String mensagemResumo) {
        this.mensagemResumo = mensagemResumo;
    }
}
