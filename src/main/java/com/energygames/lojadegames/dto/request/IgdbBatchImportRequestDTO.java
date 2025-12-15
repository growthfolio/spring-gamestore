package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO de requisição para importação em lote de jogos da IGDB
 */
public class IgdbBatchImportRequestDTO {

    @NotNull(message = "Lista de IDs não pode ser nula")
    @NotEmpty(message = "Lista de IDs não pode estar vazia")
    @Size(min = 1, max = 50, message = "A quantidade de jogos deve estar entre 1 e 50")
    private List<Long> igdbIds;

    // Getters e Setters

    public List<Long> getIgdbIds() {
        return igdbIds;
    }

    public void setIgdbIds(List<Long> igdbIds) {
        this.igdbIds = igdbIds;
    }
}
