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
		usuario.setNome(dto.getNome());
		usuario.setUsuario(dto.getUsuario());
		usuario.setSenha(dto.getSenha());
		usuario.setFoto(dto.getFoto());
		return usuario;
	}

	public void updateEntity(UsuarioRequestDTO dto, Usuario usuario) {
		usuario.setNome(dto.getNome());
		usuario.setUsuario(dto.getUsuario());
		usuario.setSenha(dto.getSenha());
		usuario.setFoto(dto.getFoto());
	}

	public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
		UsuarioResponseDTO dto = new UsuarioResponseDTO();
		dto.setId(usuario.getId());
		dto.setNome(usuario.getNome());
		dto.setUsuario(usuario.getUsuario());
		dto.setFoto(usuario.getFoto());
		return dto;
	}

	public AuthResponseDTO toAuthResponseDTO(Usuario usuario, String token) {
		AuthResponseDTO dto = new AuthResponseDTO();
		dto.setId(usuario.getId());
		dto.setNome(usuario.getNome());
		dto.setUsuario(usuario.getUsuario());
		dto.setFoto(usuario.getFoto());
		dto.setToken(token);
		return dto;
	}
}
