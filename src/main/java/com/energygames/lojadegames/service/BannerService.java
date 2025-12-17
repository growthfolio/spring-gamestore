package com.energygames.lojadegames.service;

import java.util.List;

import com.energygames.lojadegames.dto.banner.BannerRequestDTO;
import com.energygames.lojadegames.dto.banner.BannerResponseDTO;

/**
 * Service para gerenciamento de banners do carousel
 */
public interface BannerService {

    /**
     * Lista todos os banners ativos para exibição pública (respeitando período)
     */
    List<BannerResponseDTO> listarBannersAtivos();

    /**
     * Lista todos os banners para administração
     */
    List<BannerResponseDTO> listarTodos();

    /**
     * Busca um banner por ID
     */
    BannerResponseDTO buscarPorId(Long id);

    /**
     * Cria um novo banner
     */
    BannerResponseDTO criar(BannerRequestDTO request);

    /**
     * Atualiza um banner existente
     */
    BannerResponseDTO atualizar(Long id, BannerRequestDTO request);

    /**
     * Remove um banner
     */
    void deletar(Long id);

    /**
     * Ativa/desativa um banner
     */
    BannerResponseDTO toggleAtivo(Long id);

    /**
     * Reordena os banners
     */
    void reordenar(List<Long> bannerIds);

    /**
     * Cria um banner automaticamente a partir de um produto (usando artwork/screenshot)
     */
    BannerResponseDTO criarDeProduto(Long produtoId, String subtitulo);
}
