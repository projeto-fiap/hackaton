package tech.fiap.hackaton.internal.infra;

import org.apache.kafka.common.serialization.Serializer;
import tech.fiap.hackaton.internal.dto.VideoProducerDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class VideoProducerSerializer implements Serializer<VideoProducerDTO> {

	@Override
	public byte[] serialize(String topic, VideoProducerDTO data) {
		if (data == null) {
			return null;
		}

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
			// Serializa o objeto para byte array
			objectOutputStream.writeObject(data);
			objectOutputStream.flush();
			return byteArrayOutputStream.toByteArray();
		}
		catch (IOException e) {
			throw new RuntimeException("Erro ao serializar o vídeo", e);
		}
	}

}
