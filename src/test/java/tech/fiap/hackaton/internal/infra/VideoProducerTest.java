package tech.fiap.hackaton.internal.infra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.internal.dto.VideoProducerDTO;
import tech.fiap.hackaton.internal.entity.Video;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoProducerTest {

    @Mock
    private KafkaTemplate<String, VideoProducerDTO> kafkaTemplate;

    @InjectMocks
    private VideoProducer videoProducer;

    @Test
    void sendVideo_ShouldSendVideoToKafkaWithCorrectTopicAndKey() throws IOException {
        // Arrange
        String hashNome = "video-123";
        String originalFileName = "video.mp4";
        String contentType = "video/mp4";
        byte[] fileContent = "test content".getBytes();

        MultipartFile file = new MockMultipartFile("video", originalFileName, contentType, fileContent);
        Video video = new Video();
        video.setHashNome(hashNome);

        // Act
        videoProducer.sendVideo(file, video);

        // Assert
        verify(kafkaTemplate).send(
                eq("v1.video-upload-content"), // Verifica o tópico correto
                eq(hashNome), // Verifica a chave (hashNome)
                any(VideoProducerDTO.class)
        );
    }

    @Test
    void sendVideo_ShouldCreateDTOWithCorrectFields() throws IOException {
        // Arrange
        String hashNome = "video-123";
        String originalFileName = "video.mp4";
        String contentType = "video/mp4";
        byte[] fileContent = "test content".getBytes();

        MultipartFile file = new MockMultipartFile("video", originalFileName, contentType, fileContent);
        Video video = new Video();
        video.setHashNome(hashNome);

        // Act
        videoProducer.sendVideo(file, video);

        // Assert
        verify(kafkaTemplate).send(
                anyString(),
                anyString(),
                argThat(dto -> {
                    assertEquals(hashNome, dto.getFileName()); // Agora verificando hashNome
                    assertEquals(contentType, dto.getContentType());
                    assertNotNull(dto.getData());
                    return true;
                })
        );
    }

    @Test
    void sendVideo_ShouldHandleIOException() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(new IOException("Simulated error"));
        Video video = new Video();
        video.setHashNome("video-123");

        // Act & Assert
        assertThrows(IOException.class, () -> videoProducer.sendVideo(file, video));
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    void sendVideo_ShouldCompressDataBeforeSending() throws IOException {
        // Arrange
        String content = "This is a test content that should be compressed ".repeat(50);
        MultipartFile file = new MockMultipartFile("video", "video.mp4", "video/mp4", content.getBytes());
        Video video = new Video();
        video.setHashNome("video-123");

        // Act
        videoProducer.sendVideo(file, video);

        // Assert
        verify(kafkaTemplate).send(
                anyString(),
                anyString(),
                argThat(dto -> {
                    assertTrue(dto.getData().length < content.getBytes().length,
                            "Dados devem ser menores após compressão");
                    return true;
                })
        );
    }
}