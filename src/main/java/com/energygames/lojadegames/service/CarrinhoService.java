package com.energygames.lojadegames.service;

import com.energygames.lojadegames.dto.request.CarrinhoRequestDTO;
import com.energygames.lojadegames.dto.response.CarrinhoItemResponseDTO;
import com.energygames.lojadegames.dto.response.CarrinhoResumoResponseDTO;

public interface CarrinhoService {

	CarrinhoItemResponseDTO adicionar(CarrinhoRequestDTO dto);

	CarrinhoItemResponseDTO atualizarQuantidade(Long produtoId, Integer quantidade);

	void remover(Long produtoId);

	void limpar();

	CarrinhoResumoResponseDTO obterResumo();

	Integer contarItens();
}
