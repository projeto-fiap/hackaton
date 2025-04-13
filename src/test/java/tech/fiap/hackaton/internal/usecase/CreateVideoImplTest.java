package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.usecase.CreateVideo;
import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;
import tech.fiap.hackaton.internal.infra.VideoProducer;
import tech.fiap.hackaton.internal.repository.PersonRepository;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVideoImplTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private VideoProducer videoProducer;

    @InjectMocks
    private CreateVideoImpl createVideo;

    @Test
    void uploadVideos_ShouldProcessMultipleFilesSuccessfully() throws IOException {
        // Arrange
        Long personId = 1L;
        Person person = new Person();
        person.setId(personId);

        MultipartFile file1 = new MockMultipartFile("video1", "video1.mp4", "video/mp4", "content1".getBytes());
        MultipartFile file2 = new MockMultipartFile("video2", "video2.mp4", "video/mp4", "content2".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video video = invocation.getArgument(0);
            video.setId(1L); // Simula o ID gerado pelo banco
            video.setHashNome(UUID.randomUUID().toString() + "-" + video.getId());
            return video;
        });
        doNothing().when(videoProducer).sendVideo(any(MultipartFile.class), any(Video.class));

        // Act
        List<VideoDTO> result = createVideo.uploadVideos(files, personId);

        // Assert
        assertEquals(2, result.size());

        result.forEach(videoDTO -> {
            assertNotNull(videoDTO.getId());
            assertNotNull(videoDTO.getNome());
            assertEquals(VideoStatus.RECEBIDO, videoDTO.getStatus());
            assertNotNull(videoDTO.getDataCriacao());
            assertNotNull(videoDTO.getDataAtualizacao());
        });

        verify(personRepository, times(1)).findById(personId);
        verify(videoRepository, times(4)).save(any(Video.class)); // 2 vídeos × 2 saves cada
        verify(videoProducer, times(2)).sendVideo(any(MultipartFile.class), any(Video.class));
    }

    @Test
    void uploadVideos_ShouldThrowExceptionWhenPersonNotFound() {
        // Arrange
        Long personId = 99L;
        MultipartFile file = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
        List<MultipartFile> files = List.of(file);

        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> createVideo.uploadVideos(files, personId));

        assertEquals("Pessoa não encontrada", exception.getMessage());
        verify(videoRepository, never()).save(any());
        try {
            verify(videoProducer, never()).sendVideo(any(), any());
        } catch (IOException e) {
            fail("IOException não deveria ser lançada aqui");
        }
    }

    @Test
    void uploadVideos_ShouldHandleKafkaIOException() throws IOException {
        // Arrange
        Long personId = 1L;
        Person person = new Person();
        person.setId(personId);

        MultipartFile file = new MockMultipartFile("video", "video.mp4", "video/mp4", "content".getBytes());
        List<MultipartFile> files = List.of(file);

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video video = invocation.getArgument(0);
            video.setId(1L);
            video.setHashNome(UUID.randomUUID().toString() + "-" + video.getId());
            return video;
        });
        doThrow(new IOException("Kafka error")).when(videoProducer).sendVideo(any(), any());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> createVideo.uploadVideos(files, personId));

        assertEquals("Erro ao enviar vídeo para o Kafka", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
    }
}