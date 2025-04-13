package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.usecase.DownloadVideo;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DownloadVideoImplTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private DownloadVideoImpl downloadVideo;

    @Test
    void download_ShouldReturnVideoUrlWhenVideoExists() {
        // Arrange
        String hashNome = "video-123";
        String expectedUrl = "http://storage.com/video.mp4";

        Video video = new Video();
        video.setHashNome(hashNome);
        video.setUrl(expectedUrl);

        when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.of(video));

        // Act
        Optional<String> result = downloadVideo.download(hashNome);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUrl, result.get());
        verify(videoRepository, times(1)).findByHashNome(hashNome);
    }

    @Test
    void download_ShouldReturnEmptyWhenVideoNotFound() {
        // Arrange
        String hashNome = "video-nao-existente";
        when(videoRepository.findByHashNome(hashNome)).thenReturn(Optional.empty());

        // Act
        Optional<String> result = downloadVideo.download(hashNome);

        // Assert
        assertFalse(result.isPresent());
        verify(videoRepository, times(1)).findByHashNome(hashNome);
    }

    @Test
    void download_ShouldUseCorrectHashForSearch() {
        // Arrange
        String expectedHash = "video-456";
        String expectedUrl = "http://storage.com/another-video.mp4";

        Video video = new Video();
        video.setHashNome(expectedHash);
        video.setUrl(expectedUrl);

        when(videoRepository.findByHashNome(expectedHash)).thenReturn(Optional.of(video));

        // Act
        Optional<String> result = downloadVideo.download(expectedHash);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUrl, result.get());
        verify(videoRepository).findByHashNome(expectedHash);
    }
}