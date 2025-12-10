package com.energygames.lojadegames.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.response.FavoritoResponseDTO;
import com.energygames.lojadegames.service.FavoritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/favoritos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Favoritos", description = "Endpoints para gerenciar lista de favoritos do usuário")
public class FavoritoController {

	private final FavoritoService favoritoService;

	public FavoritoController(FavoritoService favoritoService) {
		this.favoritoService = favoritoService;
	}

	@PostMapping("/produto/{produtoId}")
	@Operation(summary = "Adicionar aos favoritos", description = "Adiciona um produto à lista de favoritos do usuário autenticado")
	public ResponseEntity<FavoritoResponseDTO> adicionar(@PathVariable Long produtoId) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(favoritoService.adicionar(produtoId));
	}

	@DeleteMapping("/produto/{produtoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Remover dos favoritos", description = "Remove um produto da lista de favoritos")
	public void remover(@PathVariable Long produtoId) {
		favoritoService.remover(produtoId);
	}

	@GetMapping
	@Operation(summary = "Listar meus favoritos", description = "Retorna todos os produtos favoritos do usuário autenticado com paginação")
	public ResponseEntity<Page<FavoritoResponseDTO>> listar(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "dataAdicionado,desc") String sort) {

		String[] sortParams = sort.split(",");
		Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
				? Sort.Direction.DESC : Sort.Direction.ASC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

		return ResponseEntity.ok(favoritoService.listarMeusFavoritos(pageable));
	}

	@GetMapping("/produto/{produtoId}/verificar")
	@Operation(summary = "Verificar se é favorito", description = "Verifica se um produto está nos favoritos do usuário")
	public ResponseEntity<Boolean> verificar(@PathVariable Long produtoId) {
		return ResponseEntity.ok(favoritoService.isFavorito(produtoId));
	}

	@GetMapping("/contagem")
	@Operation(summary = "Contar favoritos", description = "Retorna a quantidade de produtos nos favoritos do usuário")
	public ResponseEntity<Long> contar() {
		return ResponseEntity.ok(favoritoService.contarMeusFavoritos());
	}
}
