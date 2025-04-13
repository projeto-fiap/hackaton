package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.internal.dto.VideoStatusDTO;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetrieveVideoStatusImplTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private RetrieveVideoStatusImpl retrieveVideoStatus;

    @Test
    void getStatusByVideoId_ShouldReturnStatusWhenVideoExists() {
        // Arrange
        Long videoId = 1L;
        Video video = new Video();
        video.setId(videoId);
        video.setUrl("http://example.com/video.mp4");
        video.setStatus(VideoStatus.PROCESSANDO);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        // Act
        VideoStatusDTO result = retrieveVideoStatus.getStatusByVideoId(videoId);

        // Assert
        assertNotNull(result);
        assertEquals(videoId, result.getId());
        assertEquals(video.getUrl(), result.getUrl());
        assertEquals(video.getStatus(), result.getStatus());

        verify(videoRepository, times(1)).findById(videoId);
    }

    @Test
    void getStatusByVideoId_ShouldThrowExceptionWhenVideoNotFound() {
        // Arrange
        Long videoId = 99L;
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> retrieveVideoStatus.getStatusByVideoId(videoId));

        assertEquals("Video não encontrado", exception.getMessage());
        verify(videoRepository, times(1)).findById(videoId);
    }

    @Test
    void getStatusByVideoId_ShouldMapAllFieldsCorrectly() {
        // Arrange
        Long videoId = 2L;
        Video video = new Video();
        video.setId(videoId);
        video.setUrl("http://example.com/another-video.mp4");
        video.setStatus(VideoStatus.RECEBIDO);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        // Act
        VideoStatusDTO result = retrieveVideoStatus.getStatusByVideoId(videoId);

        // Assert
        assertAll(
                () -> assertEquals(video.getId(), result.getId()),
                () -> assertEquals(video.getUrl(), result.getUrl()),
                () -> assertEquals(video.getStatus(), result.getStatus())
        );
    }

    @Test
    void getStatusByVideoId_ShouldUseCorrectVideoIdForSearch() {
        // Arrange
        Long expectedVideoId = 3L;
        Video video = new Video();
        video.setId(expectedVideoId);
        video.setUrl("http://example.com/test-video.mp4");
        video.setStatus(VideoStatus.PROCESSANDO);

        when(videoRepository.findById(expectedVideoId)).thenReturn(Optional.of(video));

        // Act
        VideoStatusDTO result = retrieveVideoStatus.getStatusByVideoId(expectedVideoId);

        // Assert
        assertEquals(expectedVideoId, result.getId());
        verify(videoRepository).findById(expectedVideoId);
    }
}