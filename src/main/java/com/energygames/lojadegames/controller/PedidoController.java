package com.energygames.lojadegames.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.response.PedidoResponseDTO;
import com.energygames.lojadegames.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Criar pedido (Checkout)", description = "Cria um novo pedido com base nos itens do carrinho do usuário autenticado")
    public ResponseEntity<PedidoResponseDTO> criarPedido() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.criarPedido());
    }

    @GetMapping
    @Operation(summary = "Listar meus pedidos", description = "Retorna o histórico de pedidos do usuário autenticado")
    public ResponseEntity<List<PedidoResponseDTO>> listarMeusPedidos() {
        return ResponseEntity.ok(pedidoService.listarMeusPedidos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna detalhes de um pedido específico (apenas dono ou admin)")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorId(id));
    }
}
