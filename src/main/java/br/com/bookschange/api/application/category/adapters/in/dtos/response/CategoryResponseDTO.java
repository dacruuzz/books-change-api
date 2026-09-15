package br.com.bookschange.api.application.category.adapters.in.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponseDTO(
        UUID uuid,
        LocalDateTime createdAt,
        String label,
        String slug, // Same writing of label but without accentuation
        String description
) {
}
