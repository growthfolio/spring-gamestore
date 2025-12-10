package com.energygames.lojadegames.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.request.CarrinhoRequestDTO;
import com.energygames.lojadegames.dto.response.CarrinhoItemResponseDTO;
import com.energygames.lojadegames.dto.response.CarrinhoResumoResponseDTO;
import com.energygames.lojadegames.service.CarrinhoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/carrinho")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Carrinho", description = "Endpoints para gerenciar carrinho de compras")
public class CarrinhoController {

	private final CarrinhoService carrinhoService;

	public CarrinhoController(CarrinhoService carrinhoService) {
		this.carrinhoService = carrinhoService;
	}

	@PostMapping
	@Operation(summary = "Adicionar item ao carrinho", description = "Adiciona um produto ao carrinho ou incrementa quantidade se já existir")
	public ResponseEntity<CarrinhoItemResponseDTO> adicionar(@Valid @RequestBody CarrinhoRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(carrinhoService.adicionar(dto));
	}

	@PatchMapping("/produto/{produtoId}")
	@Operation(summary = "Atualizar quantidade", description = "Atualiza a quantidade de um produto no carrinho")
	public ResponseEntity<CarrinhoItemResponseDTO> atualizarQuantidade(
			@PathVariable Long produtoId,
			@RequestParam Integer quantidade) {
		return ResponseEntity.ok(carrinhoService.atualizarQuantidade(produtoId, quantidade));
	}

	@DeleteMapping("/produto/{produtoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Remover item", description = "Remove um produto do carrinho")
	public void remover(@PathVariable Long produtoId) {
		carrinhoService.remover(produtoId);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Limpar carrinho", description = "Remove todos os itens do carrinho")
	public void limpar() {
		carrinhoService.limpar();
	}

	@GetMapping
	@Operation(summary = "Obter resumo do carrinho", description = "Retorna todos os itens e totalizadores do carrinho")
	public ResponseEntity<CarrinhoResumoResponseDTO> obterResumo() {
		return ResponseEntity.ok(carrinhoService.obterResumo());
	}

	@GetMapping("/contagem")
	@Operation(summary = "Contar itens", description = "Retorna a quantidade total de produtos no carrinho")
	public ResponseEntity<Integer> contarItens() {
		return ResponseEntity.ok(carrinhoService.contarItens());
	}
}
