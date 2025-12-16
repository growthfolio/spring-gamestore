package com.energygames.lojadegames.dto.mapper;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.energygames.lojadegames.dto.request.ProdutoRequestDTO;
import com.energygames.lojadegames.dto.response.ImagemResponseDTO;
import com.energygames.lojadegames.dto.response.ProdutoDetalheResponseDTO;
import com.energygames.lojadegames.dto.response.ProdutoDetalheResponseDTO.MidiaCountDTO;
import com.energygames.lojadegames.dto.response.ProdutoDetalheResponseDTO.MidiaDTO;
import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;
import com.energygames.lojadegames.dto.response.VideoResponseDTO;
import com.energygames.lojadegames.enums.TipoImagemEnum;
import com.energygames.lojadegames.model.Categoria;
import com.energygames.lojadegames.model.Plataforma;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.ProdutoImagem;
import com.energygames.lojadegames.model.ProdutoVideo;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.service.MediaUrlService;

@Component
public class ProdutoMapper {

	private final MediaUrlService mediaUrlService;

	public ProdutoMapper(MediaUrlService mediaUrlService) {
		this.mediaUrlService = mediaUrlService;
	}

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

	// ==================== PRODUTO DETALHE (MÍDIA ESTRUTURADA) ====================

	/**
	 * Converte Produto para ProdutoDetalheResponseDTO com mídia estruturada
	 * Ideal para página de detalhe do produto com galeria de mídia
	 */
	public ProdutoDetalheResponseDTO toDetalheResponseDTO(Produto produto) {
		ProdutoDetalheResponseDTO dto = new ProdutoDetalheResponseDTO();
		
		// Campos básicos
		dto.setId(produto.getId());
		dto.setNome(produto.getNome());
		dto.setSlug(produto.getSlug());
		dto.setDescricao(produto.getDescricao());
		dto.setDescricaoCompleta(produto.getDescricaoCompleta());
		dto.setPreco(produto.getPreco());
		dto.setPrecoComDesconto(calcularPrecoComDesconto(produto.getPreco(), produto.getDesconto()));
		dto.setDesconto(produto.getDesconto());
		dto.setEstoque(produto.getEstoque());
		dto.setEmEstoque(produto.getEstoque() != null && produto.getEstoque() > 0);
		dto.setAtivo(produto.getAtivo());
		
		// Informações do jogo
		dto.setPlataforma(produto.getPlataforma());
		dto.setDesenvolvedor(produto.getDesenvolvedor());
		dto.setPublisher(produto.getPublisher());
		dto.setDataLancamento(produto.getDataLancamento());
		dto.setStatus(produto.getStatus());
		
		// Ratings
		dto.setRatingIgdb(produto.getRatingIgdb());
		dto.setTotalVotosExternos(produto.getTotalVotosExternos());
		
		// Categoria
		if (produto.getCategoria() != null) {
			dto.setCategoria(toCategoriaMidiaDTO(produto.getCategoria()));
		}
		
		// Plataformas
		if (produto.getPlataformas() != null && !produto.getPlataformas().isEmpty()) {
			dto.setPlataformas(produto.getPlataformas().stream()
				.map(this::toPlataformaResumoDTO)
				.collect(Collectors.toList()));
		}
		
		// Gêneros
		if (produto.getGeneros() != null && !produto.getGeneros().isEmpty()) {
			dto.setGeneros(produto.getGeneros().stream()
				.map(this::toGeneroResumoDTO)
				.collect(Collectors.toList()));
		}
		
		// Mídia estruturada
		dto.setMidia(buildMidiaDTO(produto));
		
		// Origem externa
		if (produto.getOrigemExterna() != null) {
			ProdutoDetalheResponseDTO.OrigemExternaDTO origem = new ProdutoDetalheResponseDTO.OrigemExternaDTO();
			origem.setOrigem(produto.getOrigemExterna().getOrigem().name());
			origem.setIdExterno(produto.getOrigemExterna().getIdExterno());
			origem.setUrlExterna(produto.getOrigemExterna().getUrlExterna());
			dto.setOrigemExterna(origem);
		}
		
		return dto;
	}

	/**
	 * Constrói MidiaDTO com imagens e vídeos estruturados
	 */
	private MidiaDTO buildMidiaDTO(Produto produto) {
		MidiaDTO midia = new MidiaDTO();
		
		if (produto.getImagensEstruturadas() != null && !produto.getImagensEstruturadas().isEmpty()) {
			List<ProdutoImagem> imagensOrdenadas = produto.getImagensEstruturadas().stream()
				.sorted(Comparator.comparing(ProdutoImagem::getOrdem, Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList());
			
			// Todas as imagens convertidas para DTO
			List<ImagemResponseDTO> todasImagens = imagensOrdenadas.stream()
				.map(mediaUrlService::toImagemResponseDTO)
				.collect(Collectors.toList());
			midia.setImagens(todasImagens);
			
			// Capa (primeira imagem principal ou tipo CAPA)
			imagensOrdenadas.stream()
				.filter(img -> img.getTipo() == TipoImagemEnum.CAPA || Boolean.TRUE.equals(img.getImagemPrincipal()))
				.findFirst()
				.ifPresent(img -> midia.setCapa(mediaUrlService.toImagemResponseDTO(img)));
			
			// Screenshots
			midia.setScreenshots(imagensOrdenadas.stream()
				.filter(img -> img.getTipo() == TipoImagemEnum.SCREENSHOT)
				.map(mediaUrlService::toImagemResponseDTO)
				.collect(Collectors.toList()));
			
			// Artworks
			midia.setArtworks(imagensOrdenadas.stream()
				.filter(img -> img.getTipo() == TipoImagemEnum.ARTWORK)
				.map(mediaUrlService::toImagemResponseDTO)
				.collect(Collectors.toList()));
		}
		
		// Vídeos
		if (produto.getVideos() != null && !produto.getVideos().isEmpty()) {
			List<VideoResponseDTO> videos = produto.getVideos().stream()
				.sorted(Comparator.comparing(ProdutoVideo::getOrdem, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(mediaUrlService::toVideoResponseDTO)
				.collect(Collectors.toList());
			midia.setVideos(videos);
		}
		
		// Contagem
		MidiaCountDTO contagem = new MidiaCountDTO();
		contagem.setTotalImagens(midia.getImagens() != null ? midia.getImagens().size() : 0);
		contagem.setScreenshots(midia.getScreenshots() != null ? midia.getScreenshots().size() : 0);
		contagem.setArtworks(midia.getArtworks() != null ? midia.getArtworks().size() : 0);
		contagem.setVideos(midia.getVideos() != null ? midia.getVideos().size() : 0);
		midia.setContagem(contagem);
		
		return midia;
	}

	private ProdutoDetalheResponseDTO.CategoriaResumoDTO toCategoriaMidiaDTO(Categoria categoria) {
		ProdutoDetalheResponseDTO.CategoriaResumoDTO dto = new ProdutoDetalheResponseDTO.CategoriaResumoDTO();
		dto.setId(categoria.getId());
		dto.setTipo(categoria.getTipo());
		dto.setIcone(categoria.getIcone());
		dto.setSlug(categoria.getSlug());
		return dto;
	}

	private ProdutoDetalheResponseDTO.PlataformaResumoDTO toPlataformaResumoDTO(Plataforma plataforma) {
		ProdutoDetalheResponseDTO.PlataformaResumoDTO dto = new ProdutoDetalheResponseDTO.PlataformaResumoDTO();
		dto.setId(plataforma.getId());
		dto.setNome(plataforma.getNome());
		dto.setAbreviacao(plataforma.getAbreviacao());
		dto.setSlug(plataforma.getSlug());
		return dto;
	}

	private ProdutoDetalheResponseDTO.GeneroResumoDTO toGeneroResumoDTO(Categoria genero) {
		ProdutoDetalheResponseDTO.GeneroResumoDTO dto = new ProdutoDetalheResponseDTO.GeneroResumoDTO();
		dto.setId(genero.getId());
		dto.setNome(genero.getTipo());
		dto.setSlug(genero.getSlug());
		return dto;
	}
}
