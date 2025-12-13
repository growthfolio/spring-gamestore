package com.energygames.lojadegames.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.request.ProdutoComercialUpdateDTO;
import com.energygames.lojadegames.dto.request.ProdutoRequestDTO;
import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;
import com.energygames.lojadegames.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "Endpoints de gerenciamento de produtos/jogos")
public class ProdutoController {

	private final ProdutoService produtoService;
	
	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@GetMapping
	public ResponseEntity<Page<ProdutoResponseDTO>> getAll(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) Long categoriaId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "nome,asc") String sort) {
		
		String[] sortParams = sort.split(",");
		Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") 
				? Sort.Direction.DESC : Sort.Direction.ASC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
		
		return ResponseEntity.ok(produtoService.buscarTodos(nome, categoriaId, pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProdutoResponseDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(produtoService.buscarPorId(id));
	}

	@GetMapping("/buscar")
	public ResponseEntity<Page<ProdutoResponseDTO>> buscar(
			@RequestParam String nome,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(produtoService.buscarTodos(nome, null, pageable));
	}

	@PostMapping
	public ResponseEntity<ProdutoResponseDTO> post(@Valid @RequestBody ProdutoRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(produtoService.criar(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProdutoResponseDTO> put(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO dto) {
		return ResponseEntity.ok(produtoService.atualizar(id, dto));
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		produtoService.deletar(id);
	}

	@Operation(summary = "Atualizar dados comerciais (Admin)", description = "Atualiza preço, estoque e status de um produto importado da IGDB")
	@SecurityRequirement(name = "bearer-key")
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{id}/comercial")
	public ResponseEntity<ProdutoResponseDTO> atualizarDadosComerciais(
			@PathVariable Long id,
			@Valid @RequestBody ProdutoComercialUpdateDTO dto) {
		return ResponseEntity.ok(produtoService.atualizarDadosComerciais(id, dto));
	}
}