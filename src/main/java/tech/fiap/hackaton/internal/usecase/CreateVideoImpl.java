package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.model.VideoResponse;
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

    @Override
    public List<VideoDTO> uploadVideos(List<MultipartFile> files, Long personId) {
        // Verificar se a pessoa existe
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        // Criar os vídeos e associá-los à pessoa
        List<Video> videos = files.stream().map(file -> {
            Video video = new Video();
            video.setNome(file.getOriginalFilename());
            video.setUrl("https://videos.example.com/" + file.getOriginalFilename());  // Exemplo de URL
            video.setStatus(VideoStatus.ENVIADO);
            video.setDataCriacao(LocalDateTime.now());
            video.setDataAtualizacao(LocalDateTime.now());
            video.setPerson(person);  // Associando o vídeo à pessoa

            // Salvar o vídeo
            return videoRepository.save(video);
        }).collect(Collectors.toList());

        // Atualiza a lista de vídeos da pessoa (já foi feita no processo de criação)
        person.getVideos().addAll(videos);

        // Salva a pessoa com a lista atualizada de vídeos
        personRepository.save(person);

        // Retorna os vídeos criados como DTO
        return videos.stream().map(video -> new VideoDTO(
                video.getId(),
                video.getNome(),
                video.getUrl(),
                video.getStatus(),
                video.getDataCriacao(),
                video.getDataAtualizacao()
        )).collect(Collectors.toList());
    }
}
