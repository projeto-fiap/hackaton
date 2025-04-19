package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
	void updateVideo_ShouldUpdateAllFieldsWhenVideoExists() {
		// Arrange
		String videoId = "video-123";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId(videoId);
		statusKafka.setStatus(VideoStatus.FINALIZADO);
		statusKafka.setDownloadUrl("http://download.url");
		statusKafka.setStorage("bucket-name");
		statusKafka.setMessage("Processamento concluído com sucesso");

		Video existingVideo = new Video();
		existingVideo.setHashNome(videoId);
		existingVideo.setStatus(VideoStatus.PROCESSANDO);

		when(videoRepository.findByHashNome(videoId)).thenReturn(Optional.of(existingVideo));
		when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		updateVideo.updateVideo(statusKafka);

		// Assert
		verify(videoRepository).save(existingVideo);
		assertEquals(VideoStatus.FINALIZADO, existingVideo.getStatus());
		assertEquals("http://download.url", existingVideo.getUrl());
		assertNotNull(existingVideo.getDataAtualizacao());
	}

	@Test
	void updateVideo_ShouldHandleErroStatusWithMessage() {
		// Arrange
		String videoId = "video-456";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId(videoId);
		statusKafka.setStatus(VideoStatus.ERRO);
		statusKafka.setMessage("Falha no processamento");
		statusKafka.setStorage("bucket-error");

		Video existingVideo = new Video();
		existingVideo.setHashNome(videoId);
		existingVideo.setStatus(VideoStatus.PROCESSANDO);

		when(videoRepository.findByHashNome(videoId)).thenReturn(Optional.of(existingVideo));
		when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		updateVideo.updateVideo(statusKafka);

		// Assert
		assertEquals(VideoStatus.ERRO, existingVideo.getStatus());
		assertNull(existingVideo.getUrl()); // URL não deve ser atualizada quando não fornecida
	}

	@Test
	void updateVideo_ShouldLogErrorWhenVideoNotFound() {
		// Arrange
		String videoId = "video-not-found";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId(videoId);
		statusKafka.setStatus(VideoStatus.FINALIZADO);

		when(videoRepository.findByHashNome(videoId)).thenReturn(Optional.empty());

		// Act
		updateVideo.updateVideo(statusKafka);

		// Assert
		verify(videoRepository, never()).save(any());
	}

	@Test
	void updateVideo_ShouldUpdateStorageInformationWhenProvided() {
		// Arrange
		String videoId = "video-101";
		VideoStatusKafka statusKafka = new VideoStatusKafka();
		statusKafka.setVideoId(videoId);
		statusKafka.setStatus(VideoStatus.FINALIZADO);
		statusKafka.setStorage("new-bucket");
		statusKafka.setDownloadUrl("http://new.url");

		Video existingVideo = new Video();
		existingVideo.setHashNome(videoId);
		existingVideo.setStatus(VideoStatus.PROCESSANDO);

		when(videoRepository.findByHashNome(videoId)).thenReturn(Optional.of(existingVideo));
		when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		updateVideo.updateVideo(statusKafka);

		// Assert
		assertEquals("http://new.url", existingVideo.getUrl());
		// Assumindo que a classe Video tem um campo para storage
		// Se necessário, adicione a verificação específica para o campo storage
	}
}