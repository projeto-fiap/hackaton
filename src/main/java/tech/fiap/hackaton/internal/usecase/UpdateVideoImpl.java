package tech.fiap.hackaton.internal.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.UpdateVideo;
import tech.fiap.hackaton.internal.dto.VideoStatusKafka;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UpdateVideoImpl implements UpdateVideo {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public void updateVideo(VideoStatusKafka videoStatusKafka) {
        Optional<Video> optionalVideo = videoRepository.findByHashNome(videoStatusKafka.getVideoId());
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            try {
                // Valida e converte o status recebido para o enum VideoStatus
                VideoStatus status = VideoStatus.valueOf(videoStatusKafka.getStatus().toUpperCase());
                video.setStatus(status);
            } catch (IllegalArgumentException e) {
                System.err.println("Status inválido recebido: " + videoStatusKafka.getStatus());
                return;
            }
            video.setUrl(videoStatusKafka.getStorage());
            video.setDataAtualizacao(LocalDateTime.now()); // Atualiza manualmente a data de atualização
            videoRepository.save(video);
            System.out.println("Vídeo atualizado com sucesso: " + video.getHashNome());
        } else {
            System.err.println("Vídeo não encontrado: " + videoStatusKafka.getVideoId());
        }
    }
}
