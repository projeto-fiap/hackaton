package tech.fiap.hackaton.api.model;

import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

public record VideoResponse(Long id,
                            String nome,
                            String url,
                            VideoStatus status) {
}
