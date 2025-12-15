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

import com.energygames.lojadegames.dto.request.CategoriaRequestDTO;
import com.energygames.lojadegames.dto.response.CategoriaResponseDTO;
import com.energygames.lojadegames.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
	
	@GetMapping
	public ResponseEntity<Page<CategoriaResponseDTO>> getAll(
			@RequestParam(required = false) String descricao,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "tipo,asc") String sort) {
		
		String[] sortParams = sort.split(",");
		Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") 
				? Sort.Direction.DESC : Sort.Direction.ASC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
		
		return ResponseEntity.ok(categoriaService.buscarTodas(descricao, pageable));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CategoriaResponseDTO> getById(@PathVariable Long id){
		return ResponseEntity.ok(categoriaService.buscarPorId(id));
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