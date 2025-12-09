package com.energygames.lojadegames.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioRequestDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@Schema(example = "email@email.com.br")
	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email deve ser válido")
	private String usuario;

	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
	private String senha;

	@Size(max = 5000, message = "URL da foto não pode ultrapassar 5000 caracteres")
	private String foto;

	// Getters e Setters
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
}
