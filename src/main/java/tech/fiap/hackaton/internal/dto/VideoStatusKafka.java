package tech.fiap.hackaton.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoStatusKafka {

	private String videoId;

	private String storage;

	private String message;

	private String downloadUrl;

	private VideoStatus status;

}
