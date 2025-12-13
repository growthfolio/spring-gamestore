package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para atualização de dados comerciais do produto (preço, estoque, desconto)
 * Usado pelo admin após importar jogos da IGDB para definir valores de venda
 */
public class ProdutoComercialUpdateDTO {

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "O estoque é obrigatório")
    @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer estoque;

    @DecimalMin(value = "0.0", message = "Desconto não pode ser negativo")
    private BigDecimal desconto;

    private Boolean ativo;

    // Getters e Setters
    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
