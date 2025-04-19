package tech.fiap.hackaton.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.fiap.hackaton.api.usecase.*;
import tech.fiap.hackaton.internal.dto.PersonWithVideoDTO;
import tech.fiap.hackaton.internal.dto.VideoDTO;
import tech.fiap.hackaton.internal.dto.VideoStatusDTO;
import java.net.URI;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/video")
public class VideoController {

	private final CreateVideo createVideo;

	private final RetrieveVideos retrieveVideos;

	private final RetrieveVideoStatus retrieveVideoStatus;

	private final GetUserByVideoHash getUserByVideoHash;

	private final DownloadVideo downloadVideo;

	public VideoController(CreateVideo createVideo, RetrieveVideos retrieveVideos,
			RetrieveVideoStatus retrieveVideoStatus, GetUserByVideoHash getUserByVideoHash,
			DownloadVideo downloadVideo) {
		this.createVideo = createVideo;
		this.retrieveVideos = retrieveVideos;
		this.retrieveVideoStatus = retrieveVideoStatus;
		this.getUserByVideoHash = getUserByVideoHash;
		this.downloadVideo = downloadVideo;
	}

	@PostMapping("/upload/{personId}")
	public List<VideoDTO> uploadVideos(@RequestParam("files") List<MultipartFile> files, @PathVariable Long personId) {
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

	@GetMapping("/download/{hashNome}")
	public ResponseEntity<?> downloadVideo(@PathVariable String hashNome) {
		Optional<String> downloadUrl = downloadVideo.download(hashNome);
		return downloadUrl.map(url -> ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build())
				.orElse(ResponseEntity.notFound().build());
	}

}
