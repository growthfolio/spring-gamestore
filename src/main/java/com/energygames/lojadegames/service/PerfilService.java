package com.energygames.lojadegames.service;

import com.energygames.lojadegames.dto.response.PerfilStatsDTO;

public interface PerfilService {
    PerfilStatsDTO getPerfilStats(Long usuarioId);
}
