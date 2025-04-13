package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.DownloadVideo;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.util.Optional;

@Service
public class DownloadVideoImpl implements DownloadVideo {

    private final VideoRepository videoRepository;

    public DownloadVideoImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public Optional<String> download(String hashNome) {
        return videoRepository.findByHashNome(hashNome)
                .map(video -> video.getUrl());
    }
}

