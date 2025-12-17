package com.energygames.lojadegames.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.banner.BannerRequestDTO;
import com.energygames.lojadegames.dto.banner.BannerResponseDTO;
import com.energygames.lojadegames.enums.TipoBannerEnum;
import com.energygames.lojadegames.enums.TipoImagemEnum;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.Banner;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.ProdutoImagem;
import com.energygames.lojadegames.repository.BannerRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.service.BannerService;

@Service
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final ProdutoRepository produtoRepository;

    public BannerServiceImpl(BannerRepository bannerRepository, ProdutoRepository produtoRepository) {
        this.bannerRepository = bannerRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> listarBannersAtivos() {
        return bannerRepository.findBannersAtivosNoPeriodo(LocalDateTime.now())
                .stream()
                .map(BannerResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> listarTodos() {
        return bannerRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Banner::getOrdem))
                .map(BannerResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponseDTO buscarPorId(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner não encontrado com ID: " + id));
        return BannerResponseDTO.fromEntity(banner);
    }

    @Override
    public BannerResponseDTO criar(BannerRequestDTO request) {
        Banner banner = new Banner();
        mapRequestToEntity(request, banner);
        
        Banner saved = bannerRepository.save(banner);
        return BannerResponseDTO.fromEntity(saved);
    }

    @Override
    public BannerResponseDTO atualizar(Long id, BannerRequestDTO request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner não encontrado com ID: " + id));
        
        mapRequestToEntity(request, banner);
        
        Banner saved = bannerRepository.save(banner);
        return BannerResponseDTO.fromEntity(saved);
    }

    @Override
    public void deletar(Long id) {
        if (!bannerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banner não encontrado com ID: " + id);
        }
        bannerRepository.deleteById(id);
    }

    @Override
    public BannerResponseDTO toggleAtivo(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner não encontrado com ID: " + id));
        
        banner.setAtivo(!banner.getAtivo());
        Banner saved = bannerRepository.save(banner);
        return BannerResponseDTO.fromEntity(saved);
    }

    @Override
    public void reordenar(List<Long> bannerIds) {
        for (int i = 0; i < bannerIds.size(); i++) {
            Long bannerId = bannerIds.get(i);
            Banner banner = bannerRepository.findById(bannerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Banner não encontrado com ID: " + bannerId));
            banner.setOrdem(i);
            bannerRepository.save(banner);
        }
    }

    @Override
    public BannerResponseDTO criarDeProduto(Long produtoId, String subtitulo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId));

        // Busca a melhor imagem para banner (prioridade: ARTWORK > SCREENSHOT > CAPA)
        String urlImagem = findBestBannerImage(produto);
        
        if (urlImagem == null) {
            throw new IllegalArgumentException("Produto não possui imagens adequadas para banner");
        }

        Banner banner = new Banner();
        banner.setTitulo(produto.getNome());
        banner.setSubtitulo(subtitulo != null ? subtitulo : produto.getDescricao());
        banner.setUrlImagem(urlImagem);
        banner.setTipo(TipoBannerEnum.PRODUTO);
        banner.setProduto(produto);
        banner.setTextoBotao("Ver jogo");
        banner.setOrdem((int) bannerRepository.count());
        banner.setAtivo(true);

        Banner saved = bannerRepository.save(banner);
        return BannerResponseDTO.fromEntity(saved);
    }

    /**
     * Encontra a melhor imagem do produto para usar como banner.
     * Prioridade: ARTWORK em 1080p > SCREENSHOT em 1080p > qualquer imagem grande
     * Usa imagensEstruturadas (ProdutoImagem) que tem tipo e idIgdb
     */
    private String findBestBannerImage(Produto produto) {
        // Primeiro verifica imagensEstruturadas (tem metadados completos)
        if (produto.getImagensEstruturadas() != null && !produto.getImagensEstruturadas().isEmpty()) {
            // Primeiro, tenta encontrar um ARTWORK
            ProdutoImagem artwork = produto.getImagensEstruturadas().stream()
                    .filter(img -> img.getTipo() == TipoImagemEnum.ARTWORK)
                    .findFirst()
                    .orElse(null);

            if (artwork != null) {
                return buildLargeImageUrl(artwork);
            }

            // Depois, tenta um SCREENSHOT
            ProdutoImagem screenshot = produto.getImagensEstruturadas().stream()
                    .filter(img -> img.getTipo() == TipoImagemEnum.SCREENSHOT)
                    .findFirst()
                    .orElse(null);

            if (screenshot != null) {
                return buildLargeImageUrl(screenshot);
            }

            // Por último, usa qualquer imagem estruturada disponível
            ProdutoImagem qualquerImagem = produto.getImagensEstruturadas().stream()
                    .findFirst()
                    .orElse(null);

            if (qualquerImagem != null) {
                return buildLargeImageUrl(qualquerImagem);
            }
        }
        
        // Fallback: usa lista simples de imagens (URLs diretas)
        if (produto.getImagens() != null && !produto.getImagens().isEmpty()) {
            return produto.getImagens().get(0);
        }
        
        return null;
    }

    /**
     * Constrói URL da imagem IGDB em alta resolução (1080p)
     */
    private String buildLargeImageUrl(ProdutoImagem imagem) {
        if (imagem.getIdIgdb() != null) {
            // Se tem ID da IGDB, monta URL em 1080p
            return String.format("https://images.igdb.com/igdb/image/upload/t_1080p/%s.jpg", imagem.getIdIgdb());
        }
        // Caso contrário, usa a URL original
        return imagem.getUrl();
    }

    private void mapRequestToEntity(BannerRequestDTO request, Banner banner) {
        banner.setTitulo(request.getTitulo());
        banner.setSubtitulo(request.getSubtitulo());
        banner.setUrlImagem(request.getUrlImagem());
        banner.setTipo(request.getTipo());
        banner.setLinkCustom(request.getLinkCustom());
        banner.setTextoBotao(request.getTextoBotao());
        banner.setOrdem(request.getOrdem() != null ? request.getOrdem() : 0);
        banner.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        banner.setDataInicio(request.getDataInicio());
        banner.setDataFim(request.getDataFim());

        if (request.getProdutoId() != null) {
            Produto produto = produtoRepository.findById(request.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.getProdutoId()));
            banner.setProduto(produto);
        } else {
            banner.setProduto(null);
        }
    }
}
