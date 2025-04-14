package tech.fiap.hackaton.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.usecase.*;
import tech.fiap.hackaton.internal.dto.PersonWithVideoDTO;
import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.dto.VideoStatusDTO;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

	@Mock
	private CreateVideo createVideo;

	@Mock
	private RetrieveVideos retrieveVideos;

	@Mock
	private RetrieveVideoStatus retrieveVideoStatus;

	@Mock
	private GetUserByVideoHash getUserByVideoHash;

	@Mock
	private DownloadVideo downloadVideo;

	@InjectMocks
	private VideoController videoController;

	@Test
	void uploadVideos_ShouldReturnListOfVideos() {
		// Arrange
		Long personId = 1L;
		MultipartFile file = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
		List<MultipartFile> files = List.of(file);

		VideoDTO videoDTO = new VideoDTO(1L, "video.mp4", "url", null, null, null);
		when(createVideo.uploadVideos(files, personId)).thenReturn(List.of(videoDTO));

		// Act
		List<VideoDTO> result = videoController.uploadVideos(files, personId);

		// Assert
		assertEquals(1, result.size());
		assertEquals(videoDTO, result.get(0));
		verify(createVideo, times(1)).uploadVideos(files, personId);
	}

	@Test
	void getVideosByPerson_ShouldReturnVideos() {
		// Arrange
		Long personId = 1L;
		VideoDTO videoDTO = new VideoDTO(1L, "video.mp4", "url", null, null, null);
		when(retrieveVideos.getVideosByPerson(personId)).thenReturn(List.of(videoDTO));

		// Act
		ResponseEntity<List<VideoDTO>> response = videoController.getVideosByPerson(personId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		assertEquals(videoDTO, response.getBody().get(0));
		verify(retrieveVideos, times(1)).getVideosByPerson(personId);
	}

	@Test
	void getVideoStatus_ShouldReturnStatus() {
		// Arrange
		Long videoId = 1L;
		VideoStatusDTO statusDTO = new VideoStatusDTO(videoId, "url", VideoStatus.PROCESSANDO);
		when(retrieveVideoStatus.getStatusByVideoId(videoId)).thenReturn(statusDTO);

		// Act
		ResponseEntity<VideoStatusDTO> response = videoController.getVideoStatus(videoId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(statusDTO, response.getBody());
		verify(retrieveVideoStatus, times(1)).getStatusByVideoId(videoId);
	}

	@Test
	void getPersonWithVideoHash_ShouldReturnPersonWithVideo() {
		// Arrange
		String hashNome = "video-123";
		PersonWithVideoDTO personWithVideoDTO = new PersonWithVideoDTO(1L, "Nome", "CPF", "email", 1L, "video.mp4",
				"url", "PROCESSANDO", null, null);
		when(getUserByVideoHash.findUserByVideoHash(hashNome)).thenReturn(personWithVideoDTO);

		// Act
		PersonWithVideoDTO result = videoController.getPersonWithVideoHash(hashNome);

		// Assert
		assertEquals(personWithVideoDTO, result);
		verify(getUserByVideoHash, times(1)).findUserByVideoHash(hashNome);
	}

	@Test
	void downloadVideo_ShouldReturnRedirectWhenVideoExists() {
		// Arrange
		String hashNome = "video-123";
		String url = "http://storage.com/video.mp4";
		when(downloadVideo.download(hashNome)).thenReturn(Optional.of(url));

		// Act
		ResponseEntity<?> response = videoController.downloadVideo(hashNome);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(URI.create(url), response.getHeaders().getLocation());
		verify(downloadVideo, times(1)).download(hashNome);
	}

	@Test
	void downloadVideo_ShouldReturnNotFoundWhenVideoDoesNotExist() {
		// Arrange
		String hashNome = "video-nao-existente";
		when(downloadVideo.download(hashNome)).thenReturn(Optional.empty());

		// Act
		ResponseEntity<?> response = videoController.downloadVideo(hashNome);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		verify(downloadVideo, times(1)).download(hashNome);
	}

}