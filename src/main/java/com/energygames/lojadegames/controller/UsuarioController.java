package com.energygames.lojadegames.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.request.LoginRequestDTO;
import com.energygames.lojadegames.dto.request.SenhaResetDTO;
import com.energygames.lojadegames.dto.request.SenhaUpdateDTO;
import com.energygames.lojadegames.dto.request.UsuarioRequestDTO;
import com.energygames.lojadegames.dto.response.AuthResponseDTO;
import com.energygames.lojadegames.dto.response.UsuarioResponseDTO;
import com.energygames.lojadegames.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<UsuarioResponseDTO>> getAll(){
		return ResponseEntity.ok(usuarioService.buscarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(usuarioService.buscarPorId(id));
	}
	
	@PostMapping("/logar")
	public ResponseEntity<AuthResponseDTO> autenticar(@Valid @RequestBody LoginRequestDTO dto){
		return ResponseEntity.ok(usuarioService.autenticar(dto));
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(usuarioService.cadastrar(dto));
	}

	@PutMapping("/atualizar/{id}")
	public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
		return ResponseEntity.ok(usuarioService.atualizar(id, dto));
	}

	@PatchMapping("/{id}/senha")
	public ResponseEntity<Void> alterarSenha(@PathVariable Long id, @Valid @RequestBody SenhaUpdateDTO dto) {
		usuarioService.alterarSenha(id, dto);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/recuperar-senha")
	public ResponseEntity<Void> recuperarSenha(@Valid @RequestBody SenhaResetDTO dto) {
		usuarioService.recuperarSenha(dto);
		return ResponseEntity.noContent().build();
	}
}