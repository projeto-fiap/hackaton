package tech.fiap.hackaton.internal.entity;

import jakarta.persistence.*;
import lombok.Data;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

@Data
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String url;

    @Enumerated(EnumType.STRING)
    private VideoStatus status;


}
