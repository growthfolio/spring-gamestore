package com.energygames.lojadegames.dto.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.energygames.lojadegames.dto.response.FavoritoResponseDTO;
import com.energygames.lojadegames.model.Favorito;
import com.energygames.lojadegames.model.Produto;

@Component
public class FavoritoMapper {

	public FavoritoResponseDTO toResponseDTO(Favorito favorito) {
		FavoritoResponseDTO dto = new FavoritoResponseDTO();
		dto.setId(favorito.getId());
		dto.setDataAdicionado(favorito.getDataAdicionado());
		dto.setProduto(toProdutoResumoDTO(favorito.getProduto()));
		return dto;
	}

	private FavoritoResponseDTO.ProdutoResumoDTO toProdutoResumoDTO(Produto produto) {
		FavoritoResponseDTO.ProdutoResumoDTO dto = new FavoritoResponseDTO.ProdutoResumoDTO();
		dto.setId(produto.getId());
		dto.setNome(produto.getNome());
		dto.setDescricao(produto.getDescricao());
		dto.setPreco(produto.getPreco());
		dto.setPrecoComDesconto(calcularPrecoComDesconto(produto.getPreco(), produto.getDesconto()));
		dto.setPlataforma(produto.getPlataforma());
		dto.setEmEstoque(produto.getEstoque() != null && produto.getEstoque() > 0);
		
		if (produto.getImagens() != null && !produto.getImagens().isEmpty()) {
			dto.setImagemPrincipal(produto.getImagens().get(0));
		}
		
		return dto;
	}

	private BigDecimal calcularPrecoComDesconto(BigDecimal preco, BigDecimal desconto) {
		if (preco == null || desconto == null || desconto.compareTo(BigDecimal.ZERO) == 0) {
			return preco;
		}
		BigDecimal percentualDesconto = desconto.divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
		return preco.subtract(preco.multiply(percentualDesconto)).setScale(2, java.math.RoundingMode.HALF_UP);
	}
}
