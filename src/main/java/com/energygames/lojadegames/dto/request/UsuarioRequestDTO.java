package com.energygames.lojadegames.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioRequestDTO {

	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
	private String nome;

	@Schema(example = "email@email.com.br")
	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email deve ser válido")
	@Size(max = 100, message = "Email não pode ultrapassar 100 caracteres")
	private String usuario;

	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
		message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número"
	)
	private String senha;

	@Size(max = 500, message = "URL da foto não pode ultrapassar 500 caracteres")
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
