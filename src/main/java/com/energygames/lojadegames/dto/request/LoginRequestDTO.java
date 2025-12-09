package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email deve ser válido")
	private String usuario;

	@NotBlank(message = "Senha é obrigatória")
	private String senha;

	// Getters e Setters
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
}
