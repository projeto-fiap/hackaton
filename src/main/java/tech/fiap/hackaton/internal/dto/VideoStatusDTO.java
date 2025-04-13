package tech.fiap.hackaton.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

@Data
@AllArgsConstructor
public class VideoStatusDTO {

	private Long id;

	private String url;

	private VideoStatus status;

}
