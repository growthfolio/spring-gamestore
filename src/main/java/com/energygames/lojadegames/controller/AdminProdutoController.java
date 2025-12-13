package com.energygames.lojadegames.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;
import com.energygames.lojadegames.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/produtos")
@Tag(name = "Admin Produtos", description = "Endpoints administrativos para gestão de produtos")
@SecurityRequirement(name = "bearer-key")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProdutoController {

    private final ProdutoService produtoService;

    public AdminProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/pendentes")
    @Operation(summary = "Listar produtos pendentes", description = "Retorna produtos importados da IGDB que ainda não foram ativados")
    public ResponseEntity<Page<ProdutoResponseDTO>> listarPendentes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataCriacao,desc") String sort) {
        
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        return ResponseEntity.ok(produtoService.buscarProdutosPendentes(pageable));
    }
}
