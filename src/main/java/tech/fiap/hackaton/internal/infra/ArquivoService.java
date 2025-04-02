package tech.fiap.hackaton.internal.infra;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ArquivoService {

    private final Path diretorioTarget = Paths.get(System.getProperty("user.dir"), "target");

    public void salvarVideo(byte[] videoData, String nomeArquivo, String contentType) throws IOException {
        // Garante que o diretório 'target' existe
        File targetDir = diretorioTarget.toFile();
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // Obtém a extensão do arquivo com base no contentType
        String extensao = obterExtensao(contentType);
        if (extensao == null) {
            throw new IOException("Tipo de conteúdo não suportado: " + contentType);
        }

        // Adiciona a extensão ao nome do arquivo
        String nomeArquivoComExtensao = nomeArquivo + extensao;

        // Cria o arquivo no diretório 'target'
        File arquivoVideo = new File(targetDir, nomeArquivoComExtensao);
        try (FileOutputStream fos = new FileOutputStream(arquivoVideo)) {
            fos.write(videoData);
            System.out.println("Vídeo salvo em: " + arquivoVideo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao salvar o vídeo: " + e.getMessage());
            throw e;
        }
    }

    private String obterExtensao(String contentType) {
        switch (contentType) {
            case "video/mp4":
                return ".mp4";
            case "video/avi":
                return ".avi";
            case "video/mpeg":
                return ".mpeg";
            // Adicione outros tipos conforme necessário
            default:
                return null; // Retorna null para tipos não suportados
        }
    }
}
