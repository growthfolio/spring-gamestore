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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.request.AvaliacaoRequestDTO;
import com.energygames.lojadegames.dto.response.AvaliacaoResponseDTO;
import com.energygames.lojadegames.service.AvaliacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/avaliacoes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Avaliações", description = "Endpoints para gerenciar avaliações de produtos")
public class AvaliacaoController {

	private final AvaliacaoService avaliacaoService;

	public AvaliacaoController(AvaliacaoService avaliacaoService) {
		this.avaliacaoService = avaliacaoService;
	}

	@PostMapping
	@Operation(summary = "Criar nova avaliação", description = "Cria uma avaliação para um produto. Usuário deve estar autenticado.")
	public ResponseEntity<AvaliacaoResponseDTO> criar(@Valid @RequestBody AvaliacaoRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(avaliacaoService.criar(dto));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar avaliação", description = "Atualiza uma avaliação existente. Apenas o autor pode atualizar.")
	public ResponseEntity<AvaliacaoResponseDTO> atualizar(
			@PathVariable Long id,
			@Valid @RequestBody AvaliacaoRequestDTO dto) {
		return ResponseEntity.ok(avaliacaoService.atualizar(id, dto));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Deletar avaliação", description = "Remove uma avaliação. Apenas o autor pode deletar.")
	public void deletar(@PathVariable Long id) {
		avaliacaoService.deletar(id);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar avaliação por ID")
	public ResponseEntity<AvaliacaoResponseDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(avaliacaoService.buscarPorId(id));
	}

	@GetMapping("/produto/{produtoId}")
	@Operation(summary = "Listar avaliações de um produto", description = "Retorna todas as avaliações de um produto com paginação")
	public ResponseEntity<Page<AvaliacaoResponseDTO>> buscarPorProduto(
			@PathVariable Long produtoId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "dataAvaliacao,desc") String sort) {

		String[] sortParams = sort.split(",");
		Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
				? Sort.Direction.DESC : Sort.Direction.ASC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

		return ResponseEntity.ok(avaliacaoService.buscarPorProduto(produtoId, pageable));
	}

	@GetMapping("/produto/{produtoId}/media")
	@Operation(summary = "Calcular média de avaliações", description = "Retorna a média das notas de um produto")
	public ResponseEntity<Double> calcularMedia(@PathVariable Long produtoId) {
		return ResponseEntity.ok(avaliacaoService.calcularMediaPorProduto(produtoId));
	}

	@GetMapping("/produto/{produtoId}/contagem")
	@Operation(summary = "Contar avaliações", description = "Retorna a quantidade de avaliações de um produto")
	public ResponseEntity<Long> contarAvaliacoes(@PathVariable Long produtoId) {
		return ResponseEntity.ok(avaliacaoService.contarAvaliacoesPorProduto(produtoId));
	}
}
