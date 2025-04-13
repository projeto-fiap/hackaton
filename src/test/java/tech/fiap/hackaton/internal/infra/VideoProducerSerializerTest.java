package tech.fiap.hackaton.internal.infra;

import org.junit.jupiter.api.Test;
import tech.fiap.hackaton.internal.dto.VideoProducerDTO;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class VideoProducerSerializerTest {

    private final VideoProducerSerializer serializer = new VideoProducerSerializer();

    @Test
    void serialize_ShouldReturnNullForNullInput() {
        // Arrange
        String topic = "test-topic";

        // Act
        byte[] result = serializer.serialize(topic, null);

        // Assert
        assertNull(result);
    }

    @Test
    void serialize_ShouldReturnSerializedBytesForValidInput() {
        // Arrange
        String topic = "test-topic";
        VideoProducerDTO data = new VideoProducerDTO();
        data.setFileName("video.mp4");
        data.setContentType("video/mp4");
        data.setData(new byte[]{1, 2, 3, 4, 5});

        // Act
        byte[] result = serializer.serialize(topic, data);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void serialize_ShouldThrowRuntimeExceptionOnSerializationError() {
        // Arrange
        String topic = "test-topic";
        VideoProducerDTO data = new VideoProducerDTO() {
            // Classe anônima para forçar erro de serialização
            private void writeObject(java.io.ObjectOutputStream out) throws IOException {
                throw new IOException("Simulated serialization error");
            }
        };

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> serializer.serialize(topic, data));

        assertEquals("Erro ao serializar o vídeo", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
        assertEquals("Simulated serialization error", exception.getCause().getMessage());
    }

    @Test
    void serialize_ShouldHandleEmptyData() {
        // Arrange
        String topic = "test-topic";
        VideoProducerDTO data = new VideoProducerDTO();
        data.setFileName("video.mp4");
        data.setContentType("video/mp4");
        data.setData(new byte[0]); // Dados vazios

        // Act
        byte[] result = serializer.serialize(topic, data);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0); // O objeto serializado deve ter conteúdo mesmo com dados vazios
    }
}