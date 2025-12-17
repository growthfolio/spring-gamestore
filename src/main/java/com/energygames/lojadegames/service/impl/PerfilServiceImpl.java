package com.energygames.lojadegames.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.energygames.lojadegames.dto.response.PerfilStatsDTO;
import com.energygames.lojadegames.dto.response.PerfilStatsDTO.CompraResumoDTO;
import com.energygames.lojadegames.dto.response.PerfilStatsDTO.ConquistaDTO;
import com.energygames.lojadegames.dto.response.PerfilStatsDTO.GeneroFavoritoDTO;
import com.energygames.lojadegames.enums.ConquistaEnum;
import com.energygames.lojadegames.enums.NivelUsuarioEnum;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.Pedido;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.AvaliacaoRepository;
import com.energygames.lojadegames.repository.CarrinhoRepository;
import com.energygames.lojadegames.repository.FavoritoRepository;
import com.energygames.lojadegames.repository.PedidoRepository;
import com.energygames.lojadegames.repository.UsuarioRepository;
import com.energygames.lojadegames.service.PerfilService;

@Service
public class PerfilServiceImpl implements PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final FavoritoRepository favoritoRepository;
    private final CarrinhoRepository carrinhoRepository;

    public PerfilServiceImpl(
            UsuarioRepository usuarioRepository,
            PedidoRepository pedidoRepository,
            AvaliacaoRepository avaliacaoRepository,
            FavoritoRepository favoritoRepository,
            CarrinhoRepository carrinhoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.favoritoRepository = favoritoRepository;
        this.carrinhoRepository = carrinhoRepository;
    }

    @Override
    public PerfilStatsDTO getPerfilStats(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        PerfilStatsDTO stats = new PerfilStatsDTO();
        stats.setUsuarioId(usuario.getId());
        stats.setNickname(usuario.getNickname());
        stats.setEmail(usuario.getEmail());
        stats.setFoto(usuario.getFoto());

        // Estatísticas básicas
        int totalCompras = pedidoRepository.contarPedidosPorUsuario(usuarioId);
        int totalAvaliacoes = avaliacaoRepository.contarAvaliacoesPorUsuario(usuarioId);
        Long totalFavoritos = favoritoRepository.contarFavoritosPorUsuario(usuarioId);
        Integer totalCarrinho = carrinhoRepository.somarQuantidadePorUsuario(usuarioId);
        BigDecimal totalGasto = pedidoRepository.somarValorTotalPorUsuario(usuarioId);

        stats.setTotalCompras(totalCompras);
        stats.setTotalAvaliacoes(totalAvaliacoes);
        stats.setTotalFavoritos(totalFavoritos != null ? totalFavoritos.intValue() : 0);
        stats.setTotalItensCarrinho(totalCarrinho != null ? totalCarrinho : 0);
        stats.setTotalGasto(totalGasto != null ? totalGasto : BigDecimal.ZERO);

        // Nível e progresso
        NivelUsuarioEnum nivel = NivelUsuarioEnum.calcularNivel(totalCompras);
        NivelUsuarioEnum proximoNivel = nivel.getProximoNivel();
        stats.setNivel(nivel);
        stats.setProximoNivel(proximoNivel);

        if (nivel != proximoNivel) {
            int comprasAtuais = totalCompras - nivel.getComprasMinimas();
            int comprasNecessarias = proximoNivel.getComprasMinimas() - nivel.getComprasMinimas();
            stats.setComprasParaProximoNivel(proximoNivel.getComprasMinimas() - totalCompras);
            stats.setProgressoNivel(comprasNecessarias > 0 ? (comprasAtuais * 100) / comprasNecessarias : 100);
        } else {
            stats.setComprasParaProximoNivel(0);
            stats.setProgressoNivel(100);
        }

        // Conquistas
        List<ConquistaDTO> conquistas = calcularConquistas(totalCompras, totalAvaliacoes, 
                totalFavoritos != null ? totalFavoritos.intValue() : 0);
        stats.setConquistas(conquistas);
        stats.setTotalConquistas((int) conquistas.stream().filter(ConquistaDTO::isDesbloqueada).count());

        // Gêneros favoritos
        stats.setGenerosFavoritos(calcularGenerosFavoritos(usuarioId, totalCompras));

        // Últimas compras
        stats.setUltimasCompras(buscarUltimasCompras(usuarioId));

        return stats;
    }

    private List<ConquistaDTO> calcularConquistas(int compras, int avaliacoes, int favoritos) {
        List<ConquistaDTO> conquistas = new ArrayList<>();

        for (ConquistaEnum c : ConquistaEnum.values()) {
            ConquistaDTO dto = new ConquistaDTO();
            dto.setCodigo(c.name());
            dto.setNome(c.getNome());
            dto.setDescricao(c.getDescricao());
            dto.setIcone(c.getIcone());
            dto.setMeta(c.getMeta());

            int progresso = 0;
            switch (c.getTipo()) {
                case "compras" -> progresso = compras;
                case "avaliacoes" -> progresso = avaliacoes;
                case "favoritos" -> progresso = favoritos;
                case "tempo" -> progresso = 0; // Simplificado - pode ser calculado com data de criação
            }

            dto.setProgresso(Math.min(progresso, c.getMeta()));
            dto.setDesbloqueada(progresso >= c.getMeta());
            conquistas.add(dto);
        }

        return conquistas;
    }

    private List<GeneroFavoritoDTO> calcularGenerosFavoritos(Long usuarioId, int totalCompras) {
        List<GeneroFavoritoDTO> generos = new ArrayList<>();
        List<Object[]> resultados = pedidoRepository.contarComprasPorCategoria(usuarioId);

        for (Object[] row : resultados) {
            if (generos.size() >= 5) break;
            
            GeneroFavoritoDTO dto = new GeneroFavoritoDTO();
            dto.setNome(row[0] != null ? row[0].toString() : "Outros");
            dto.setQuantidade(((Number) row[1]).intValue());
            dto.setPercentual(totalCompras > 0 ? (dto.getQuantidade() * 100) / totalCompras : 0);
            generos.add(dto);
        }

        return generos;
    }

    private List<CompraResumoDTO> buscarUltimasCompras(Long usuarioId) {
        List<CompraResumoDTO> compras = new ArrayList<>();
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioId, PageRequest.of(0, 5));

        for (Pedido pedido : pedidos) {
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                var item = pedido.getItens().get(0);
                CompraResumoDTO dto = new CompraResumoDTO();
                dto.setPedidoId(pedido.getId());
                dto.setProdutoNome(item.getProduto().getNome());
                dto.setData(pedido.getDataCriacao());
                dto.setValor(pedido.getValorTotal());
                
                if (item.getProduto().getImagens() != null && !item.getProduto().getImagens().isEmpty()) {
                    dto.setProdutoImagem(item.getProduto().getImagens().get(0));
                }
                compras.add(dto);
            }
        }

        return compras;
    }
}
