package tech.fiap.hackaton.internal.infra;

import org.apache.kafka.common.serialization.Deserializer;
import tech.fiap.hackaton.internal.dto.VideoProducerDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class VideoProducerDeserializer implements Deserializer<VideoProducerDTO> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public VideoProducerDTO deserialize(String topic, byte[] data) {
		if (data == null) {
			return null;
		}
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
				ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
			return (VideoProducerDTO) objectInputStream.readObject();
		}
		catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Erro ao desserializar o vídeo", e);
		}
	}

	@Override
	public void close() {
	}

}
