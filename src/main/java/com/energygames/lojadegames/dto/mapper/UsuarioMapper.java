package com.energygames.lojadegames.dto.mapper;

import org.springframework.stereotype.Component;

import com.energygames.lojadegames.dto.request.UsuarioRequestDTO;
import com.energygames.lojadegames.dto.response.AuthResponseDTO;
import com.energygames.lojadegames.dto.response.UsuarioResponseDTO;
import com.energygames.lojadegames.model.Usuario;

@Component
public class UsuarioMapper {

	public Usuario toEntity(UsuarioRequestDTO dto) {
		Usuario usuario = new Usuario();
		usuario.setNickname(dto.getNickname());
		usuario.setEmail(dto.getEmail());
		usuario.setSenha(dto.getSenha());
		usuario.setFoto(dto.getFoto());
		return usuario;
	}

	public void updateEntity(UsuarioRequestDTO dto, Usuario usuario) {
		usuario.setNickname(dto.getNickname());
		usuario.setEmail(dto.getEmail());
		usuario.setSenha(dto.getSenha());
		usuario.setFoto(dto.getFoto());
	}

	public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
		UsuarioResponseDTO dto = new UsuarioResponseDTO();
		dto.setId(usuario.getId());
		dto.setNickname(usuario.getNickname());
		dto.setEmail(usuario.getEmail());
		dto.setFoto(usuario.getFoto());
		return dto;
	}

	public AuthResponseDTO toAuthResponseDTO(Usuario usuario, String token) {
		AuthResponseDTO dto = new AuthResponseDTO();
		dto.setId(usuario.getId());
		dto.setNickname(usuario.getNickname());
		dto.setEmail(usuario.getEmail());
		dto.setFoto(usuario.getFoto());
		dto.setToken(token);
		dto.setRoles(usuario.getRoles());
		return dto;
	}
}
