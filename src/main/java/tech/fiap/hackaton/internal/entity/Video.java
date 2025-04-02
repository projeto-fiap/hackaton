package tech.fiap.hackaton.internal.entity;

import jakarta.persistence.*;
import lombok.Data;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

import java.time.LocalDateTime;

@Data
@Entity
public class Video {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String hashNome;

	private String url;

	@Enumerated(EnumType.STRING)
	private VideoStatus status;

	private LocalDateTime dataCriacao;

	private LocalDateTime dataAtualizacao;

	@ManyToOne
	@JoinColumn(name = "person_id", nullable = false)
	private Person person;

}
