package com.energygames.lojadegames.dto.mapper;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.energygames.lojadegames.dto.request.ProdutoRequestDTO;
import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;
import com.energygames.lojadegames.model.Categoria;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.ProdutoImagem;
import com.energygames.lojadegames.model.Usuario;

@Component
public class ProdutoMapper {

	public Produto toEntity(ProdutoRequestDTO dto, Categoria categoria, Usuario usuario) {
		Produto produto = new Produto();
		produto.setNome(dto.getNome());
		produto.setDescricao(dto.getDescricao());
		produto.setPreco(dto.getPreco());
		produto.setDesconto(dto.getDesconto());
		produto.setEstoque(dto.getEstoque());
		produto.setPlataforma(dto.getPlataforma());
		produto.setDesenvolvedor(dto.getDesenvolvedor());
		produto.setPublisher(dto.getPublisher());
		produto.setDataLancamento(dto.getDataLancamento());
		produto.setImagens(dto.getImagens());
		produto.setCategoria(categoria);
		produto.setUsuario(usuario);
		produto.setAtivo(true);
		return produto;
	}

	public void updateEntity(ProdutoRequestDTO dto, Produto produto, Categoria categoria) {
		produto.setNome(dto.getNome());
		produto.setDescricao(dto.getDescricao());
		produto.setPreco(dto.getPreco());
		produto.setDesconto(dto.getDesconto());
		produto.setEstoque(dto.getEstoque());
		produto.setPlataforma(dto.getPlataforma());
		produto.setDesenvolvedor(dto.getDesenvolvedor());
		produto.setPublisher(dto.getPublisher());
		produto.setDataLancamento(dto.getDataLancamento());
		produto.setImagens(dto.getImagens());
		produto.setCategoria(categoria);
	}

	public ProdutoResponseDTO toResponseDTO(Produto produto) {
		ProdutoResponseDTO dto = new ProdutoResponseDTO();
		dto.setId(produto.getId());
		dto.setNome(produto.getNome());
		dto.setDescricao(produto.getDescricao());
		dto.setPreco(produto.getPreco());
		dto.setPrecoComDesconto(calcularPrecoComDesconto(produto.getPreco(), produto.getDesconto()));
		dto.setDesconto(produto.getDesconto());
		dto.setEstoque(produto.getEstoque());
		dto.setEmEstoque(produto.getEstoque() != null && produto.getEstoque() > 0);
		dto.setPlataforma(produto.getPlataforma());
		dto.setDesenvolvedor(produto.getDesenvolvedor());
		dto.setPublisher(produto.getPublisher());
		dto.setDataLancamento(produto.getDataLancamento());
		
		// Prioriza imagens estruturadas (IGDB) sobre imagens simples
		dto.setImagens(extrairUrlsImagens(produto));
		dto.setAtivo(produto.getAtivo());

		if (produto.getCategoria() != null) {
			dto.setCategoria(toCategoriaResumoDTO(produto.getCategoria()));
		}

		return dto;
	}

	/**
	 * Extrai URLs das imagens, priorizando imagens estruturadas (IGDB)
	 * Se não houver estruturadas, usa o campo simples de imagens
	 */
	private List<String> extrairUrlsImagens(Produto produto) {
		// Prioriza imagens estruturadas (vindas da IGDB)
		if (produto.getImagensEstruturadas() != null && !produto.getImagensEstruturadas().isEmpty()) {
			return produto.getImagensEstruturadas().stream()
				.sorted(Comparator.comparing(ProdutoImagem::getOrdem, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(ProdutoImagem::getUrl)
				.collect(Collectors.toList());
		}
		
		// Fallback para campo simples de imagens
		return produto.getImagens();
	}

	private BigDecimal calcularPrecoComDesconto(BigDecimal preco, BigDecimal desconto) {
		if (preco == null || desconto == null || desconto.compareTo(BigDecimal.ZERO) == 0) {
			return preco;
		}
		BigDecimal percentualDesconto = desconto.divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
		return preco.subtract(preco.multiply(percentualDesconto)).setScale(2, java.math.RoundingMode.HALF_UP);
	}

	private ProdutoResponseDTO.CategoriaResumoDTO toCategoriaResumoDTO(Categoria categoria) {
		ProdutoResponseDTO.CategoriaResumoDTO dto = new ProdutoResponseDTO.CategoriaResumoDTO();
		dto.setId(categoria.getId());
		dto.setTipo(categoria.getTipo());
		dto.setIcone(categoria.getIcone());
		return dto;
	}
}
