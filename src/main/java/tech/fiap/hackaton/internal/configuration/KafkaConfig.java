package tech.fiap.hackaton.internal.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import tech.fiap.hackaton.internal.infra.VideoProducerSerializer;
import tech.fiap.hackaton.internal.dto.VideoProducerDTO;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Bean
	public ProducerFactory<String, VideoProducerDTO> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VideoProducerSerializer.class);
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 52428800);
		configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 52428800);
		configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 52428800); // 50MB
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, VideoProducerDTO> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

}
