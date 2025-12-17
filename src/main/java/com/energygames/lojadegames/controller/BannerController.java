package com.energygames.lojadegames.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.banner.BannerRequestDTO;
import com.energygames.lojadegames.dto.banner.BannerResponseDTO;
import com.energygames.lojadegames.service.BannerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/banners")
@Tag(name = "Banners", description = "Gerenciamento de banners do carousel")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    /**
     * Endpoint público - lista banners ativos para o carousel
     */
    @GetMapping("/ativos")
    @Operation(summary = "Lista banners ativos para o carousel (público)")
    public ResponseEntity<List<BannerResponseDTO>> listarBannersAtivos() {
        return ResponseEntity.ok(bannerService.listarBannersAtivos());
    }

    /**
     * Endpoint admin - lista todos os banners
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os banners (admin)")
    public ResponseEntity<List<BannerResponseDTO>> listarTodos() {
        return ResponseEntity.ok(bannerService.listarTodos());
    }

    /**
     * Endpoint admin - busca banner por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Busca banner por ID (admin)")
    public ResponseEntity<BannerResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.buscarPorId(id));
    }

    /**
     * Endpoint admin - cria novo banner
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cria novo banner (admin)")
    public ResponseEntity<BannerResponseDTO> criar(@Valid @RequestBody BannerRequestDTO request) {
        BannerResponseDTO banner = bannerService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(banner);
    }

    /**
     * Endpoint admin - cria banner a partir de um produto
     */
    @PostMapping("/produto/{produtoId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cria banner a partir de um produto (admin)")
    public ResponseEntity<BannerResponseDTO> criarDeProduto(
            @PathVariable Long produtoId,
            @RequestParam(required = false) String subtitulo) {
        BannerResponseDTO banner = bannerService.criarDeProduto(produtoId, subtitulo);
        return ResponseEntity.status(HttpStatus.CREATED).body(banner);
    }

    /**
     * Endpoint admin - atualiza banner existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualiza banner existente (admin)")
    public ResponseEntity<BannerResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody BannerRequestDTO request) {
        return ResponseEntity.ok(bannerService.atualizar(id, request));
    }

    /**
     * Endpoint admin - deleta banner
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deleta banner (admin)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        bannerService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint admin - ativa/desativa banner
     */
    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ativa/desativa banner (admin)")
    public ResponseEntity<BannerResponseDTO> toggleAtivo(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.toggleAtivo(id));
    }

    /**
     * Endpoint admin - reordena banners
     */
    @PostMapping("/reordenar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reordena banners (admin)")
    public ResponseEntity<Void> reordenar(@RequestBody List<Long> bannerIds) {
        bannerService.reordenar(bannerIds);
        return ResponseEntity.ok().build();
    }
}
