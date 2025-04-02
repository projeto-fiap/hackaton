package tech.fiap.hackaton.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoProducerDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;

	private String contentType;

	private byte[] data;

}
