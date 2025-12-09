package com.energygames.lojadegames.scheduler;

import com.energygames.lojadegames.configuration.IgdbConfigProperties;
import com.energygames.lojadegames.service.igdb.IgdbImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Scheduler para sincronização automática de produtos IGDB
 * Executa diariamente à 1h da manhã (horário de Brasília)
 */
@Component
public class IgdbSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(IgdbSyncScheduler.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final IgdbImportService importService;
    private final IgdbConfigProperties config;

    private LocalDateTime ultimaExecucao;
    private int ultimoResultado = 0;

    public IgdbSyncScheduler(IgdbImportService importService, IgdbConfigProperties config) {
        this.importService = importService;
        this.config = config;
    }

    /**
     * Sincronização diária automática às 1h da manhã
     * Cron: segundo minuto hora dia mês dia-da-semana
     * "0 0 1 * * *" = 1h00 todos os dias
     */
    @Scheduled(cron = "0 0 1 * * *", zone = "America/Sao_Paulo")
    public void sincronizarProdutosDesatualizados() {
        if (!config.isSyncEnabled()) {
            log.debug("Sincronização automática IGDB está desabilitada");
            return;
        }

        if (!config.hasCredentials()) {
            log.warn("Credenciais IGDB não configuradas. Sincronização cancelada.");
            return;
        }

        log.info("=== INÍCIO DA SINCRONIZAÇÃO AUTOMÁTICA IGDB ===");
        log.info("Data/Hora: {}", LocalDateTime.now().format(FORMATTER));
        log.info("Intervalo de desatualização: {} dias", config.getSyncIntervalDays());

        try {
            // Verifica se API está disponível
            if (!importService.checkApiStatus()) {
                log.error("API IGDB não está disponível. Sincronização cancelada.");
                return;
            }

            // Sincroniza produtos desatualizados
            int diasDesatualizacao = config.getSyncIntervalDays();
            int produtosSincronizados = importService.syncOutdatedProducts(diasDesatualizacao);

            ultimaExecucao = LocalDateTime.now();
            ultimoResultado = produtosSincronizados;

            log.info("=== SINCRONIZAÇÃO CONCLUÍDA COM SUCESSO ===");
            log.info("Produtos sincronizados: {}", produtosSincronizados);
            log.info("Horário de conclusão: {}", ultimaExecucao.format(FORMATTER));

        } catch (Exception e) {
            log.error("=== ERRO NA SINCRONIZAÇÃO AUTOMÁTICA ===", e);
            log.error("Mensagem: {}", e.getMessage());
            ultimaExecucao = LocalDateTime.now();
            ultimoResultado = -1; // Indica erro
        }
    }

    /**
     * Execução manual da sincronização (pode ser chamado via endpoint)
     * @return Número de produtos sincronizados
     */
    public int executarSincronizacaoManual() {
        if (!config.hasCredentials()) {
            throw new IllegalStateException("Credenciais IGDB não configuradas");
        }

        log.info("=== SINCRONIZAÇÃO MANUAL INICIADA ===");
        
        try {
            int diasDesatualizacao = config.getSyncIntervalDays();
            int produtosSincronizados = importService.syncOutdatedProducts(diasDesatualizacao);

            ultimaExecucao = LocalDateTime.now();
            ultimoResultado = produtosSincronizados;

            log.info("=== SINCRONIZAÇÃO MANUAL CONCLUÍDA ===");
            log.info("Produtos sincronizados: {}", produtosSincronizados);

            return produtosSincronizados;

        } catch (Exception e) {
            log.error("Erro na sincronização manual", e);
            ultimaExecucao = LocalDateTime.now();
            ultimoResultado = -1;
            throw e;
        }
    }

    /**
     * Retorna data/hora da última execução
     */
    public LocalDateTime getUltimaExecucao() {
        return ultimaExecucao;
    }

    /**
     * Retorna resultado da última execução
     * @return Número de produtos sincronizados, ou -1 se houve erro
     */
    public int getUltimoResultado() {
        return ultimoResultado;
    }

    /**
     * Verifica se sincronização está habilitada
     */
    public boolean isSincronizacaoHabilitada() {
        return config.isSyncEnabled() && config.hasCredentials();
    }
}
