package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.usecase.UpdateVideo;
import tech.fiap.hackaton.internal.dto.VideoStatusKafka;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateVideoImplTest {

	@Mock
	private VideoRepository videoRepository;

	@InjectMocks
	private UpdateVideoImpl updateVideo;

	@Test
	void updateVideo_ShouldUpdateVideoWhenFoundAndStatusValid() {
		// Arrange
		String hashNome = "video-123";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId(hashNome);
		statusKafka.setStatus("processando");
		statusKafka.setStorage("http://storage.com/video.mp4");

		Video video = new Video();
		video.setHashNome(hashNome);

		when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.of(video));
		when(videoRepository.save(any(Video.class))).thenReturn(video);

		// Act
		updateVideo.updateVideo(statusKafka);

		// Assert
		verify(videoRepository, times(1)).findByHashNome(hashNome);
		verify(videoRepository, times(1)).save(video);
		assertEquals(VideoStatus.PROCESSANDO, video.getStatus());
		assertEquals(statusKafka.getStorage(), video.getUrl());
		assertNotNull(video.getDataAtualizacao());
	}

	@Test
	void updateVideo_ShouldHandleInvalidStatus() {
		// Arrange
		String hashNome = "video-123";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId(hashNome);
		statusKafka.setStatus("status-invalido");
		statusKafka.setStorage("http://storage.com/video.mp4");

		Video video = new Video();
		video.setHashNome(hashNome);

		when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.of(video));

		// Act
		updateVideo.updateVideo(statusKafka);

		// Assert
		verify(videoRepository, times(1)).findByHashNome(hashNome);
		verify(videoRepository, never()).save(any());
	}

	@Test
	void updateVideo_ShouldHandleVideoNotFound() {
		// Arrange
		String hashNome = "video-nao-existente";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId(hashNome);
		statusKafka.setStatus("processando");
		statusKafka.setStorage("http://storage.com/video.mp4");

		when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.empty());

		// Act
		updateVideo.updateVideo(statusKafka);

		// Assert
		verify(videoRepository, times(1)).findByHashNome(hashNome);
		verify(videoRepository, never()).save(any());
	}

	@Test
	void updateVideo_ShouldUpdateAllFieldsCorrectly() {
		// Arrange
		String hashNome = "video-456";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId(hashNome);
		statusKafka.setStatus("recebido");
		statusKafka.setStorage("http://storage.com/new-video.mp4");

		Video video = new Video();
		video.setHashNome(hashNome);

		when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.of(video));
		when(videoRepository.save(any(Video.class))).thenReturn(video);

		// Act
		updateVideo.updateVideo(statusKafka);

		// Assert
		assertAll(() -> assertEquals(VideoStatus.RECEBIDO, video.getStatus()),
				() -> assertEquals(statusKafka.getStorage(), video.getUrl()),
				() -> assertNotNull(video.getDataAtualizacao()));
	}

}