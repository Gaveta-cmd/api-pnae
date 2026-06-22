package com.pnae.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioEstoqueDTO(
        EscolaResumoDTO escola,
        List<EstoqueItemResponseDTO> itensEmEstoque,
        List<EstoqueItemResponseDTO> itensVencidos,
        List<EstoqueItemResponseDTO> itensAlerta,
        BigDecimal custoTotal
) {}
