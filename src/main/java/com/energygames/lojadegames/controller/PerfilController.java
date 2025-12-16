package com.energygames.lojadegames.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.response.PerfilStatsDTO;
import com.energygames.lojadegames.security.UserDetailsImpl;
import com.energygames.lojadegames.service.PerfilService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/perfil")
@Tag(name = "Perfil", description = "Endpoints do perfil do usuário")
@SecurityRequirement(name = "bearerAuth")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Obter estatísticas do perfil", description = "Retorna estatísticas, nível, conquistas e histórico do usuário logado")
    public ResponseEntity<PerfilStatsDTO> getStats(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(perfilService.getPerfilStats(userDetails.getId()));
    }
}
