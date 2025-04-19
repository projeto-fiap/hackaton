package tech.fiap.hackaton.internal.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.internal.dto.VideoProducerDTO;
import tech.fiap.hackaton.internal.entity.Video;

import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoProducerTest {

	@Mock
	private KafkaTemplate<String, String> kafkaTemplate;

	@InjectMocks
	private VideoProducer videoProducer;

	@Test
	void sendVideo_ShouldCompressAndSendCorrectData() throws IOException {
		// Arrange
		String hashNome = "video-123";
		String contentType = "video/mp4";
		// Conteúdo maior para garantir compressão efetiva
		String content = "Este é um conteúdo de teste que deve ser comprimido eficientemente ".repeat(10);
		byte[] fileContent = content.getBytes();

		MultipartFile file = new MockMultipartFile("video", "video.mp4", contentType, fileContent);
		Video video = new Video();
		video.setHashNome(hashNome);

		ArgumentCaptor<String> kafkaMessageCaptor = ArgumentCaptor.forClass(String.class);

		// Act
		videoProducer.sendVideo(file, video);

		// Assert
		verify(kafkaTemplate).send(eq("v1.video-upload-content"), kafkaMessageCaptor.capture());

		String sentMessage = kafkaMessageCaptor.getValue();
		VideoProducerDTO dto = new ObjectMapper().readValue(sentMessage, VideoProducerDTO.class);

		assertEquals(hashNome, dto.getFilename());
		assertEquals(contentType, dto.getContentType());

		// Verificação mais flexível para compressão
		byte[] decodedData = Base64.getDecoder().decode(dto.getData());
		assertTrue(decodedData.length <= fileContent.length,
				"Dados compactados não devem ser maiores que os originais");
	}

	@Test
	void sendVideo_ShouldHandleEmptyFile() throws IOException {
		// Arrange
		MultipartFile file = new MockMultipartFile("video", "video.mp4", "video/mp4", new byte[0]);
		Video video = new Video();
		video.setHashNome("video-empty");

		ArgumentCaptor<String> kafkaMessageCaptor = ArgumentCaptor.forClass(String.class);

		// Act
		videoProducer.sendVideo(file, video);

		// Assert
		verify(kafkaTemplate).send(eq("v1.video-upload-content"), kafkaMessageCaptor.capture());

		String sentMessage = kafkaMessageCaptor.getValue();
		VideoProducerDTO dto = new ObjectMapper().readValue(sentMessage, VideoProducerDTO.class);

		assertEquals("video-empty", dto.getFilename());
		assertEquals("video/mp4", dto.getContentType());

		// Verificação modificada para dados vazios compactados
		byte[] decodedData = Base64.getDecoder().decode(dto.getData());
		assertTrue(decodedData.length > 0, "Dados vazios compactados devem ter cabeçalho GZIP");
	}

	@Test
	void sendVideo_ShouldActuallyCompressLargeData() throws IOException {
		// Arrange
		// Conteúdo grande o suficiente para ser comprimido
		String content = "Este conteúdo é grande o suficiente para ser comprimido significativamente ".repeat(100);
		byte[] fileContent = content.getBytes();

		MultipartFile file = new MockMultipartFile("video", "video.mp4", "video/mp4", fileContent);
		Video video = new Video();
		video.setHashNome("video-compressed");

		ArgumentCaptor<String> kafkaMessageCaptor = ArgumentCaptor.forClass(String.class);

		// Act
		videoProducer.sendVideo(file, video);

		// Assert
		verify(kafkaTemplate).send(eq("v1.video-upload-content"), kafkaMessageCaptor.capture());

		String sentMessage = kafkaMessageCaptor.getValue();
		VideoProducerDTO dto = new ObjectMapper().readValue(sentMessage, VideoProducerDTO.class);

		byte[] decodedData = Base64.getDecoder().decode(dto.getData());

		// Verificação mais rigorosa para grandes conjuntos de dados
		assertTrue(decodedData.length < fileContent.length * 0.9,
				"Dados grandes deveriam ser comprimidos para pelo menos 10% menor");

		// Verificação do formato GZIP
		assertDoesNotThrow(() -> {
			java.util.zip.GZIPInputStream gis = new java.util.zip.GZIPInputStream(
					new java.io.ByteArrayInputStream(decodedData));
			gis.readAllBytes();
			gis.close();
		});
	}

	@Test
	void sendVideo_ShouldThrowIOExceptionWhenFileReadFails() throws IOException {
		// Arrange
		MultipartFile file = mock(MultipartFile.class);
		when(file.getBytes()).thenThrow(new IOException("Failed to read file"));
		Video video = new Video();
		video.setHashNome("video-999");

		// Act & Assert
		assertThrows(IOException.class, () -> videoProducer.sendVideo(file, video));
		verify(kafkaTemplate, never()).send(any(), any());
	}
}