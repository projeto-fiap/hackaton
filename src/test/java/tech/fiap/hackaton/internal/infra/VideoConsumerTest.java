package tech.fiap.hackaton.internal.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.usecase.UpdateVideo;
import tech.fiap.hackaton.internal.dto.VideoStatusKafka;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoConsumerTest {

	@Mock
	private UpdateVideo updateVideo;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private VideoConsumer videoConsumer;

	@Test
	void consumeVideoStatus_ShouldProcessValidMessageSuccessfully() throws JsonProcessingException {
		// Arrange
		String message = "{\"videoId\":\"video-123\",\"status\":\"PROCESSANDO\",\"storage\":\"http://storage.com/video.mp4\"}";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId("video-123");
		statusKafka.setStatus(VideoStatus.valueOf("PROCESSANDO"));
		statusKafka.setStorage("http://storage.com/video.mp4");

		when(objectMapper.readValue(message, VideoStatusKafka.class)).thenReturn(statusKafka);

		// Act
		videoConsumer.consumeVideoStatus(message);

		// Assert
		verify(objectMapper, times(1)).readValue(message, VideoStatusKafka.class);
		verify(updateVideo, times(1)).updateVideo(statusKafka);
	}

	@Test
	void consumeVideoStatus_ShouldHandleJsonProcessingException() throws JsonProcessingException {
		// Arrange
		String invalidMessage = "invalid-json";
		when(objectMapper.readValue(invalidMessage, VideoStatusKafka.class))
				.thenThrow(new JsonProcessingException("Invalid JSON") {
				});

		// Act
		videoConsumer.consumeVideoStatus(invalidMessage);

		// Assert
		verify(objectMapper, times(1)).readValue(invalidMessage, VideoStatusKafka.class);
		verify(updateVideo, never()).updateVideo(any());
	}

	@Test
	void consumeVideoStatus_ShouldHandleRuntimeException() throws JsonProcessingException {
		// Arrange
		String message = "{\"videoId\":\"video-123\",\"status\":\"PROCESSANDO\",\"storage\":\"http://storage.com/video.mp4\"}";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId("video-123");
		statusKafka.setStatus(VideoStatus.valueOf("PROCESSANDO"));
		statusKafka.setStorage("http://storage.com/video.mp4");

		when(objectMapper.readValue(message, VideoStatusKafka.class)).thenReturn(statusKafka);
		doThrow(new RuntimeException("Update failed")).when(updateVideo).updateVideo(statusKafka);

		// Act
		videoConsumer.consumeVideoStatus(message);

		// Assert
		verify(objectMapper, times(1)).readValue(message, VideoStatusKafka.class);
		verify(updateVideo, times(1)).updateVideo(statusKafka);
	}

}