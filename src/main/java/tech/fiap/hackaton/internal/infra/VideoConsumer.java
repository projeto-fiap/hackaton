package tech.fiap.hackaton.internal.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tech.fiap.hackaton.internal.dto.VideoProducerDTO;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.IOUtils;

@Service
public class VideoConsumer {

    @Autowired
    private ArquivoService arquivoService;

    @KafkaListener(topics = "v1.video-upload-content", groupId = "video-service")
    public void consumeVideo(VideoProducerDTO videoDTO) {
        System.out.println("Recebendo vídeo: " + videoDTO.getFileName());
        System.out.println(" Tipo: " + videoDTO.getContentType());

        try {
            byte[] decompressedData = decompress(videoDTO.getData());
            arquivoService.salvarVideo(decompressedData, videoDTO.getFileName(), videoDTO.getContentType());
            System.out.println("Vídeo salvo com sucesso!");
        } catch (IOException e) {
            System.err.println(" Erro ao salvar o vídeo: " + e.getMessage());
        }
    }

    private byte[] decompress(byte[] compressedData) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             GZIPInputStream gis = new GZIPInputStream(bis)) {
            return IOUtils.toByteArray(gis);
        }
    }

}
