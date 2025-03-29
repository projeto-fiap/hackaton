package tech.fiap.hackaton.api.usecase;

import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.model.VideoResponse;
import tech.fiap.hackaton.internal.dto.VideoDTO;

import java.util.List;

public interface CreateVideo {
    List<VideoDTO> uploadVideos(List<MultipartFile> files, Long personId);
}
