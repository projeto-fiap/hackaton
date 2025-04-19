package tech.fiap.hackaton.internal.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tech.fiap.hackaton.internal.dto.VideoProducerDTO;
import tech.fiap.hackaton.internal.entity.Video;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

@Service
public class VideoProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public VideoProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendVideo(MultipartFile file, Video video) throws IOException {
		byte[] fileBytes = file.getBytes();

		byte[] compressedBytes = compress(fileBytes);

		String base64Encoded = Base64.getEncoder().encodeToString(compressedBytes);

		VideoProducerDTO videoDTO = new VideoProducerDTO(video.getHashNome(), file.getContentType(), base64Encoded);

		String json = new ObjectMapper().writeValueAsString(videoDTO);
		kafkaTemplate.send("v1.video-upload-content", json);
	}

	// Método para compactação GZIP
	private byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(data.length);
		try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
			gzipOutputStream.write(data);
		}
		return byteArrayOutputStream.toByteArray();
	}

}
