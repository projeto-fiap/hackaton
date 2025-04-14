package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.usecase.GetUserByVideoHash;
import tech.fiap.hackaton.internal.dto.PersonWithVideoDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByVideoHashImplTest {

	@Mock
	private VideoRepository videoRepository;

	@InjectMocks
	private GetUserByVideoHashImpl getUserByVideoHash;

	@Test
	void findUserByVideoHash_ShouldReturnPersonWithVideoWhenVideoExists() {
		// Arrange
		String hashNome = "video-123";
		LocalDateTime now = LocalDateTime.now();

		Person person = new Person();
		person.setId(1L);
		person.setNome("João Silva");
		person.setCpf("123.456.789-00");
		person.setEmail("joao@example.com");

		Video video = new Video();
		video.setId(1L);
		video.setNome("video.mp4");
		video.setUrl("http://storage.com/video.mp4");
		video.setStatus(VideoStatus.PROCESSANDO);
		video.setDataCriacao(now);
		video.setDataAtualizacao(now);
		video.setPerson(person);

		when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.of(video));

		// Act
		PersonWithVideoDTO result = getUserByVideoHash.findUserByVideoHash(hashNome);

		// Assert
		assertNotNull(result);
		assertEquals(person.getId(), result.getPersonId());
		assertEquals(person.getNome(), result.getPersonNome());
		assertEquals(person.getCpf(), result.getPersonCpf());
		assertEquals(person.getEmail(), result.getPersonEmail());
		assertEquals(video.getId(), result.getVideoId());
		assertEquals(video.getNome(), result.getVideoNome());
		assertEquals(video.getUrl(), result.getVideoUrl());
		assertEquals(video.getStatus().name(), result.getVideoStatus());
		assertEquals(video.getDataCriacao(), result.getVideoDataCriacao());
		assertEquals(video.getDataAtualizacao(), result.getVideoDataAtualizacao());

		verify(videoRepository, times(1)).findByHashNome(hashNome);
	}

	@Test
	void findUserByVideoHash_ShouldThrowExceptionWhenVideoNotFound() {
		// Arrange
		String hashNome = "video-nao-existente";
		when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.empty());

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> getUserByVideoHash.findUserByVideoHash(hashNome));

		assertEquals("Vídeo não encontrado", exception.getMessage());
		verify(videoRepository, times(1)).findByHashNome(hashNome);
	}

	@Test
	void findUserByVideoHash_ShouldMapAllFieldsCorrectly() {
		// Arrange
		String hashNome = "video-456";
		LocalDateTime now = LocalDateTime.now();

		Person person = new Person();
		person.setId(2L);
		person.setNome("Maria Souza");
		person.setCpf("987.654.321-00");
		person.setEmail("maria@example.com");

		Video video = new Video();
		video.setId(2L);
		video.setNome("outro-video.mp4");
		video.setUrl("http://storage.com/outro-video.mp4");
		video.setStatus(VideoStatus.RECEBIDO);
		video.setDataCriacao(now.minusDays(1));
		video.setDataAtualizacao(now);
		video.setPerson(person);

		when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.of(video));

		// Act
		PersonWithVideoDTO result = getUserByVideoHash.findUserByVideoHash(hashNome);

		// Assert
		assertAll(() -> assertEquals(person.getId(), result.getPersonId()),
				() -> assertEquals(person.getNome(), result.getPersonNome()),
				() -> assertEquals(person.getCpf(), result.getPersonCpf()),
				() -> assertEquals(person.getEmail(), result.getPersonEmail()),
				() -> assertEquals(video.getId(), result.getVideoId()),
				() -> assertEquals(video.getNome(), result.getVideoNome()),
				() -> assertEquals(video.getUrl(), result.getVideoUrl()),
				() -> assertEquals(video.getStatus().name(), result.getVideoStatus()),
				() -> assertEquals(video.getDataCriacao(), result.getVideoDataCriacao()),
				() -> assertEquals(video.getDataAtualizacao(), result.getVideoDataAtualizacao()));
	}

}