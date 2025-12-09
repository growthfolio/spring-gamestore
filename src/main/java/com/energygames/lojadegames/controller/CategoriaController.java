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

import com.energygames.lojadegames.dto.request.CategoriaRequestDTO;
import com.energygames.lojadegames.dto.response.CategoriaResponseDTO;
import com.energygames.lojadegames.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
	
	@GetMapping
	public ResponseEntity<List<CategoriaResponseDTO>> getAll(){
		return ResponseEntity.ok(categoriaService.buscarTodas());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CategoriaResponseDTO> getById(@PathVariable Long id){
		return ResponseEntity.ok(categoriaService.buscarPorId(id));
	}
	
	@GetMapping("/descricao/{descricao}")
	public ResponseEntity<List<CategoriaResponseDTO>> getByDescricao(@PathVariable String descricao){
		return ResponseEntity.ok(categoriaService.buscarPorDescricao(descricao));
	}
	 
	@PostMapping
	public ResponseEntity<CategoriaResponseDTO> post(@Valid @RequestBody CategoriaRequestDTO dto){
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(categoriaService.criar(dto));
	}
	 
	@PutMapping("/{id}")
	public ResponseEntity<CategoriaResponseDTO> put(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto){
		return ResponseEntity.ok(categoriaService.atualizar(id, dto));
	}
	 
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		categoriaService.deletar(id);
	}
}