package tech.fiap.hackaton.internal.infra;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tech.fiap.hackaton.internal.dto.VideoProducerDTO;
import tech.fiap.hackaton.internal.entity.Video;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

@Service
public class VideoProducer {

	private final KafkaTemplate<String, VideoProducerDTO> kafkaTemplate;

	public VideoProducer(KafkaTemplate<String, VideoProducerDTO> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendVideo(MultipartFile file, Video video) throws IOException {
		byte[] fileBytes = file.getBytes();
		byte[] compressedBytes = compress(fileBytes);

		VideoProducerDTO videoDTO = new VideoProducerDTO(video.getHashNome(), file.getContentType(), compressedBytes);
		kafkaTemplate.send("v1.video-upload-content", video.getHashNome(), videoDTO);
	}

	private byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(data.length);
		try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
			gzipOutputStream.write(data);
		}
		return byteArrayOutputStream.toByteArray();
	}

}
