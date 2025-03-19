package tech.fiap.hackaton.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VideoDTO {
    private Long id;
    private String nome;
    private String url;
    private VideoStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

}
