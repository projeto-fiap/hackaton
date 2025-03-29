package tech.fiap.hackaton.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.usecase.CreateVideo;
import tech.fiap.hackaton.api.usecase.GetUserByVideoHash;
import tech.fiap.hackaton.api.usecase.RetrieveVideoStatus;
import tech.fiap.hackaton.api.usecase.RetrieveVideos;
import tech.fiap.hackaton.internal.dto.PersonWithVideoDTO;
import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.dto.VideoStatusDTO;

import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    private final CreateVideo createVideo;
    private final RetrieveVideos retrieveVideos;
    private final RetrieveVideoStatus retrieveVideoStatus;
    private final GetUserByVideoHash getUserByVideoHash;

    public VideoController(CreateVideo createVideo, RetrieveVideos retrieveVideos, RetrieveVideoStatus retrieveVideoStatus, GetUserByVideoHash getUserByVideoHash) {
        this.createVideo = createVideo;
        this.retrieveVideos = retrieveVideos;
        this.retrieveVideoStatus = retrieveVideoStatus;
        this.getUserByVideoHash = getUserByVideoHash;
    }

    @PostMapping("/upload/{personId}")
    public List<VideoDTO> uploadVideos(@RequestParam("files") List<MultipartFile> files,
                                       @PathVariable Long personId) {
        return createVideo.uploadVideos(files, personId);
    }

    @GetMapping("/{personId}")
    public ResponseEntity<List<VideoDTO>> getVideosByPerson(@PathVariable Long personId) {
        List<VideoDTO> videos = retrieveVideos.getVideosByPerson(personId);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/status/{videoId}")
    public ResponseEntity<VideoStatusDTO> getVideoStatus(@PathVariable Long videoId) {
        VideoStatusDTO status = retrieveVideoStatus.getStatusByVideoId(videoId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/hash/{hashNome}")
    public PersonWithVideoDTO getPersonWithVideoHash(@PathVariable String hashNome) {
        return getUserByVideoHash.findUserByVideoHash(hashNome);
    }

}


