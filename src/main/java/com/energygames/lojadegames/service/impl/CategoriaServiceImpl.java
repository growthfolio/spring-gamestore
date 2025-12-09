package com.energygames.lojadegames.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.mapper.CategoriaMapper;
import com.energygames.lojadegames.dto.request.CategoriaRequestDTO;
import com.energygames.lojadegames.dto.response.CategoriaResponseDTO;
import com.energygames.lojadegames.exception.BusinessException;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.Categoria;
import com.energygames.lojadegames.repository.CategoriaRepository;
import com.energygames.lojadegames.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService {

	private static final Logger log = LoggerFactory.getLogger(CategoriaServiceImpl.class);

	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;

	public CategoriaServiceImpl(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
		this.categoriaRepository = categoriaRepository;
		this.categoriaMapper = categoriaMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoriaResponseDTO> buscarTodas() {
		log.info("Buscando todas as categorias");
		return categoriaRepository.findAll().stream()
				.map(categoriaMapper::toResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public CategoriaResponseDTO buscarPorId(Long id) {
		log.info("Buscando categoria com ID: {}", id);
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
		return categoriaMapper.toResponseDTO(categoria);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoriaResponseDTO> buscarPorDescricao(String descricao) {
		log.info("Buscando categorias por descrição: {}", descricao);
		return categoriaRepository.findAllByDescricaoContainingIgnoreCase(descricao).stream()
				.map(categoriaMapper::toResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
		log.info("Criando nova categoria: {}", dto.getTipo());
		
		Categoria categoria = categoriaMapper.toEntity(dto);
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		log.info("Categoria criada com sucesso. ID: {}", categoriaSalva.getId());
		return categoriaMapper.toResponseDTO(categoriaSalva);
	}

	@Override
	@Transactional
	public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
		log.info("Atualizando categoria ID: {}", id);
		
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
		
		categoriaMapper.updateEntity(dto, categoria);
		Categoria categoriaAtualizada = categoriaRepository.save(categoria);
		
		log.info("Categoria atualizada com sucesso. ID: {}", id);
		return categoriaMapper.toResponseDTO(categoriaAtualizada);
	}

	@Override
	@Transactional
	public void deletar(Long id) {
		log.info("Deletando categoria ID: {}", id);
		
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
		
		// Validar se categoria tem produtos ativos
		if (categoria.getProduto() != null && !categoria.getProduto().isEmpty()) {
			long produtosAtivos = categoria.getProduto().stream()
					.filter(p -> p.getAtivo() != null && p.getAtivo())
					.count();
			if (produtosAtivos > 0) {
				throw new BusinessException("Não é possível deletar categoria com produtos ativos");
			}
		}
		
		categoriaRepository.deleteById(id);
		log.info("Categoria deletada com sucesso. ID: {}", id);
	}
}
