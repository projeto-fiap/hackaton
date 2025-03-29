package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.usecase.CreateVideo;
import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.entity.enums.VideoStatus;
import tech.fiap.hackaton.internal.repository.PersonRepository;
import tech.fiap.hackaton.internal.repository.VideoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreateVideoImpl implements CreateVideo {

    private final VideoRepository videoRepository;
    private final PersonRepository personRepository;

    public CreateVideoImpl(VideoRepository videoRepository, PersonRepository personRepository) {
        this.videoRepository = videoRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    @Override
    public List<VideoDTO> uploadVideos(List<MultipartFile> files, Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        List<Video> videos = files.stream()
                .map(file -> createVideo(file, person))
                .collect(Collectors.toList());

        person.getVideos().addAll(videos);
        personRepository.save(person);

        return videos.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private Video createVideo(MultipartFile file, Person person) {
        Video video = new Video();
        video.setNome(file.getOriginalFilename());
        video.setUrl(file.getOriginalFilename());
        video.setStatus(VideoStatus.RECEBIDO);
        video.setDataCriacao(LocalDateTime.now());
        video.setDataAtualizacao(LocalDateTime.now());
        video.setPerson(person);
        return videoRepository.save(video);
    }

    private VideoDTO convertToDTO(Video video) {
        return new VideoDTO(
                video.getId(),
                video.getNome(),
                video.getUrl(),
                video.getStatus(),
                video.getDataCriacao(),
                video.getDataAtualizacao()
        );
    }
}
