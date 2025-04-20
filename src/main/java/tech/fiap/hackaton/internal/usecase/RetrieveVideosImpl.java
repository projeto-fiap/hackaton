package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.RetrieveVideos;
import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RetrieveVideosImpl implements RetrieveVideos {

	private final VideoRepository videoRepository;

	public RetrieveVideosImpl(VideoRepository videoRepository) {
		this.videoRepository = videoRepository;
	}

	@Override
	public List<VideoDTO> getVideosByPerson(Long personId) {
		List<Video> videos = videoRepository.findByPersonId(personId);

		return videos.stream().map(video -> new VideoDTO(video.getId(), video.getHashNome(), video.getUrl(),
				video.getStatus(), video.getDataCriacao(), video.getDataAtualizacao())).collect(Collectors.toList());
	}

}
