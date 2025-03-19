package tech.fiap.hackaton.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.usecase.CreateVideo;
import tech.fiap.hackaton.api.usecase.RetrieveVideos;
import tech.fiap.hackaton.internal.dto.VideoDTO;

import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    private final CreateVideo createVideo;
    private final RetrieveVideos retrieveVideos;

    public VideoController(CreateVideo createVideo, RetrieveVideos retrieveVideos) {
        this.createVideo = createVideo;
        this.retrieveVideos = retrieveVideos;
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
}


