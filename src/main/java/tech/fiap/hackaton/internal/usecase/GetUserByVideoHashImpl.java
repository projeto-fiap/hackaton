package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.fiap.hackaton.api.usecase.GetUserByVideoHash;
import tech.fiap.hackaton.internal.dto.PersonWithVideoDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.entity.Video;
import tech.fiap.hackaton.internal.repository.VideoRepository;

@Service
public class GetUserByVideoHashImpl implements GetUserByVideoHash {
    private final VideoRepository videoRepository;

    public GetUserByVideoHashImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public PersonWithVideoDTO findUserByVideoHash(String hashNome) {
        Video video = videoRepository.findByHashNome(hashNome)
                .orElseThrow(() -> new RuntimeException("Vídeo não encontrado"));

        Person person = video.getPerson();

        return new PersonWithVideoDTO(
                person.getId(),
                person.getNome(),
                person.getCpf(),
                person.getEmail(),
                video.getId(),
                video.getNome(),
                video.getUrl(),
                video.getStatus().name(),
                video.getDataCriacao(),
                video.getDataAtualizacao()
        );
    }
}
