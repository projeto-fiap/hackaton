package tech.fiap.hackaton.internal.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.UpdateVideo;
import tech.fiap.hackaton.internal.dto.VideoStatusKafka;

@Service
public class VideoConsumer {

    private final UpdateVideo updateVideo;
    private final ObjectMapper objectMapper;

    public VideoConsumer(UpdateVideo updateVideo, ObjectMapper objectMapper) {
        this.updateVideo = updateVideo;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "v1.video-status", groupId = "video-service")
    public void consumeVideoStatus(String message) {
        try {

            VideoStatusKafka videoStatusKafka = objectMapper.readValue(message, VideoStatusKafka.class);


            updateVideo.updateVideo(videoStatusKafka);

            System.out.println("Vídeo atualizado com sucesso: " + videoStatusKafka.getVideoId());
        } catch (Exception e) {
            System.err.println("Erro ao processar a mensagem do Kafka: " + e.getMessage());
        }
    }
}
