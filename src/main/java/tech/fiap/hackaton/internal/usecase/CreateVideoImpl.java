package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.usecase.CreateVideo;
import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.dto.VideoProducerDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;
import tech.fiap.hackaton.internal.infra.VideoProducer;
import tech.fiap.hackaton.internal.repository.PersonRepository;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateVideoImpl implements CreateVideo {

	private final VideoRepository videoRepository;

	private final PersonRepository personRepository;

	private final VideoProducer videoProducer;

	public CreateVideoImpl(VideoRepository videoRepository, PersonRepository personRepository,
			VideoProducer videoProducer) {
		this.videoRepository = videoRepository;
		this.personRepository = personRepository;
		this.videoProducer = videoProducer;
	}

	@Transactional
	@Override
	public List<VideoDTO> uploadVideos(List<MultipartFile> files, Long personId) {
		Person person = personRepository.findById(personId)
				.orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

		List<VideoDTO> videoDTOs = files.stream().map(file -> {
			Video video = createVideo(file, person);

			person.getVideos().add(video);
			personRepository.save(person);

			try {
				videoProducer.sendVideo(file, video);
			}
			catch (IOException e) {
				throw new RuntimeException("Erro ao enviar vídeo para o Kafka", e);
			}
			catch (Exception e) {
				throw new RuntimeException("Erro ao enviar vídeo para o Kafka", e);
			}

			return convertToDTO(video);
		}).collect(Collectors.toList());

		return videoDTOs;
	}

	private Video createVideo(MultipartFile file, Person person) {
		Video video = new Video();
		video.setNome(file.getOriginalFilename());
		video.setUrl(file.getOriginalFilename());
		video.setStatus(VideoStatus.RECEBIDO);
		video.setDataCriacao(LocalDateTime.now());
		video.setDataAtualizacao(LocalDateTime.now());
		video.setPerson(person);
		Video videoSalvo = videoRepository.save(video);
		videoSalvo.setHashNome(UUID.randomUUID().toString() + "-" + videoSalvo.getId());
		return videoRepository.save(videoSalvo);
	}

	private VideoDTO convertToDTO(Video video) {
		return new VideoDTO(video.getId(), video.getNome(), video.getUrl(), video.getStatus(), video.getDataCriacao(),
				video.getDataAtualizacao());
	}

}
