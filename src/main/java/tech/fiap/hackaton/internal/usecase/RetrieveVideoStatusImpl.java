package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.RetrieveVideoStatus;
import tech.fiap.hackaton.internal.dto.VideoStatusDTO;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.repository.VideoRepository;

@Service
public class RetrieveVideoStatusImpl implements RetrieveVideoStatus {

    private final VideoRepository videoRepository;

    public RetrieveVideoStatusImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public VideoStatusDTO getStatusByVideoId(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video não encontrado"));

        return new VideoStatusDTO(
                video.getId(),
                video.getUrl(),
                video.getStatus()
        );
    }
}
