package tech.fiap.hackaton.api.usecase;

import tech.fiap.hackaton.internal.dto.VideoDTO;

import java.util.List;

public interface RetrieveVideos {
    List<VideoDTO> getVideosByPerson(Long personId);
}
