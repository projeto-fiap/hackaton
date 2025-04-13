package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.usecase.RetrieveVideos;
import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetrieveVideosImplTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private RetrieveVideosImpl retrieveVideos;

    @Test
    void getVideosByPerson_ShouldReturnEmptyListWhenNoVideosFound() {
        // Arrange
        Long personId = 1L;
        when(videoRepository.findByPersonId(personId)).thenReturn(List.of());

        // Act
        List<VideoDTO> result = retrieveVideos.getVideosByPerson(personId);

        // Assert
        assertTrue(result.isEmpty());
        verify(videoRepository, times(1)).findByPersonId(personId);
    }

    @Test
    void getVideosByPerson_ShouldReturnListOfVideos() {
        // Arrange
        Long personId = 1L;
        Video video1 = createTestVideo(1L, "video1.mp4", VideoStatus.RECEBIDO);
        Video video2 = createTestVideo(2L, "video2.mp4", VideoStatus.PROCESSANDO);

        when(videoRepository.findByPersonId(personId)).thenReturn(List.of(video1, video2));

        // Act
        List<VideoDTO> result = retrieveVideos.getVideosByPerson(personId);

        // Assert
        assertEquals(2, result.size());

        // Verify first video
        VideoDTO dto1 = result.get(0);
        assertEquals(video1.getId(), dto1.getId());
        assertEquals(video1.getNome(), dto1.getNome());
        assertEquals(video1.getStatus(), dto1.getStatus());

        // Verify second video
        VideoDTO dto2 = result.get(1);
        assertEquals(video2.getId(), dto2.getId());
        assertEquals(video2.getNome(), dto2.getNome());
        assertEquals(video2.getStatus(), dto2.getStatus());

        verify(videoRepository, times(1)).findByPersonId(personId);
    }

    @Test
    void getVideosByPerson_ShouldMapAllFieldsCorrectly() {
        // Arrange
        Long personId = 1L;
        Video video = createTestVideo(1L, "video.mp4", VideoStatus.RECEBIDO);
        when(videoRepository.findByPersonId(personId)).thenReturn(List.of(video));

        // Act
        List<VideoDTO> result = retrieveVideos.getVideosByPerson(personId);
        VideoDTO videoDTO = result.get(0);

        // Assert
        assertAll(
                () -> assertEquals(video.getId(), videoDTO.getId()),
                () -> assertEquals(video.getNome(), videoDTO.getNome()),
                () -> assertEquals(video.getUrl(), videoDTO.getUrl()),
                () -> assertEquals(video.getStatus(), videoDTO.getStatus()),
                () -> assertEquals(video.getDataCriacao(), videoDTO.getDataCriacao()),
                () -> assertEquals(video.getDataAtualizacao(), videoDTO.getDataAtualizacao())
        );
    }

    private Video createTestVideo(Long id, String nome, VideoStatus status) {
        Video video = new Video();
        video.setId(id);
        video.setNome(nome);
        video.setUrl("/videos/" + nome);
        video.setStatus(status);
        video.setDataCriacao(LocalDateTime.now());
        video.setDataAtualizacao(LocalDateTime.now());
        return video;
    }
}