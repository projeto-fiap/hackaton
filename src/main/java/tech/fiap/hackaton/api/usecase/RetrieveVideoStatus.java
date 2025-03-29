package tech.fiap.hackaton.api.usecase;

import tech.fiap.hackaton.internal.dto.VideoStatusDTO;

public interface RetrieveVideoStatus {

    VideoStatusDTO getStatusByVideoId(Long videoId);
}
