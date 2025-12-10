package com.energygames.lojadegames.service;

import java.util.List;

import com.energygames.lojadegames.dto.request.LoginRequestDTO;
import com.energygames.lojadegames.dto.request.UsuarioRequestDTO;
import com.energygames.lojadegames.dto.response.AuthResponseDTO;
import com.energygames.lojadegames.dto.response.UsuarioResponseDTO;

public interface UsuarioService {
	
	List<UsuarioResponseDTO> buscarTodos();
	
	UsuarioResponseDTO buscarPorId(Long id);
	
	UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto);
	
	UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto);
	
	AuthResponseDTO autenticar(LoginRequestDTO dto);
}