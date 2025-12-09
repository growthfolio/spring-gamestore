package com.energygames.lojadegames.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.request.ProdutoRequestDTO;
import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;
import com.energygames.lojadegames.service.ProdutoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {

	private final ProdutoService produtoService;
	
	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@GetMapping
	public ResponseEntity<List<ProdutoResponseDTO>> getAll() {
		return ResponseEntity.ok(produtoService.buscarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProdutoResponseDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(produtoService.buscarPorId(id));
	}

	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<ProdutoResponseDTO>> getByNome(@PathVariable String nome) {
		return ResponseEntity.ok(produtoService.buscarPorNome(nome));
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
}