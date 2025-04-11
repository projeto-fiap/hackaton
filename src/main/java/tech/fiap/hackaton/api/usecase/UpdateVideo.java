package tech.fiap.hackaton.api.usecase;

import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.dto.VideoStatusKafka;

public interface UpdateVideo {
    void updateVideo(VideoStatusKafka videoStatusKafka);
}
