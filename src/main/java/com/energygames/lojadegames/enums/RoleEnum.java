package com.energygames.lojadegames.enums;

/**
 * Enum de roles do sistema com códigos inteiros para persistência.
 * 
 * IMPORTANTE: Nunca alterar ou remover códigos existentes!
 * Novos valores devem usar códigos sequenciais crescentes.
 * 
 * Histórico:
 * - 1: ROLE_USER (Usuário comum - desde v1.0)
 * - 2: ROLE_ADMIN (Administrador - desde v1.0)
 */
public enum RoleEnum {
	ROLE_USER(1, "Usuário Comum"),
	ROLE_ADMIN(2, "Administrador");
	
	private final Integer code;
	private final String description;
	
	RoleEnum(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * Converte código inteiro para RoleEnum.
	 * Usado pelo AttributeConverter para ler do banco de dados.
	 * 
	 * @param code Código da role
	 * @return RoleEnum correspondente
	 * @throws IllegalArgumentException se código não existir
	 */
	public static RoleEnum fromCode(Integer code) {
		if (code == null) {
			return null;
		}
		
		for (RoleEnum role : RoleEnum.values()) {
			if (role.getCode().equals(code)) {
				return role;
			}
		}
		
		throw new IllegalArgumentException("Código de role inválido: " + code);
	}
	
	/**
	 * Retorna o nome da role para Spring Security.
	 * 
	 * @return Nome da role (ex: "ROLE_USER")
	 */
	public String getAuthority() {
		return this.name();
	}
}
