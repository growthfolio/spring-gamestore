package com.energygames.lojadegames.dto.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.energygames.lojadegames.dto.response.CarrinhoItemResponseDTO;
import com.energygames.lojadegames.model.CarrinhoItem;
import com.energygames.lojadegames.model.Produto;

@Component
public class CarrinhoMapper {

	public CarrinhoItemResponseDTO toItemResponseDTO(CarrinhoItem item) {
		CarrinhoItemResponseDTO dto = new CarrinhoItemResponseDTO();
		dto.setId(item.getId());
		dto.setProdutoId(item.getProduto().getId());
		dto.setProdutoNome(item.getProduto().getNome());
		dto.setPlataforma(item.getProduto().getPlataforma());
		dto.setPrecoUnitario(item.getPrecoUnitario());
		dto.setDescontoUnitario(item.getDescontoUnitario());
		dto.setPrecoComDesconto(calcularPrecoComDesconto(item.getPrecoUnitario(), item.getDescontoUnitario()));
		dto.setQuantidade(item.getQuantidade());
		dto.setSubtotal(item.calcularSubtotal());
		dto.setDataAdicionado(item.getDataAdicionado());
		
		Produto produto = item.getProduto();
		dto.setDisponivelEstoque(produto.getEstoque() != null && produto.getEstoque() >= item.getQuantidade());
		dto.setEstoqueDisponivel(produto.getEstoque());
		
		if (produto.getImagens() != null && !produto.getImagens().isEmpty()) {
			dto.setProdutoImagem(produto.getImagens().get(0));
		}
		
		return dto;
	}

	private BigDecimal calcularPrecoComDesconto(BigDecimal preco, BigDecimal desconto) {
		if (preco == null || desconto == null || desconto.compareTo(BigDecimal.ZERO) == 0) {
			return preco;
		}
		BigDecimal percentualDesconto = desconto.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
		return preco.subtract(preco.multiply(percentualDesconto)).setScale(2, java.math.RoundingMode.HALF_UP);
	}
}
