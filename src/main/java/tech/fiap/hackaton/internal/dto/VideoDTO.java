package tech.fiap.hackaton.internal.dto;

import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

public class VideoDTO {
    private String nome;
    private String url;
    private VideoStatus status;
    private Long personId;
}
