package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SenhaResetDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
