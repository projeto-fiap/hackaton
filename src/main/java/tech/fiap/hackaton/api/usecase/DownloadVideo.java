package tech.fiap.hackaton.api.usecase;

import java.util.Optional;

public interface DownloadVideo {
    Optional<String> download(String hashNome);
}
