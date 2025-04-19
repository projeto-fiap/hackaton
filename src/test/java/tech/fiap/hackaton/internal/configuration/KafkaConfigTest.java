package tech.fiap.hackaton.internal.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    @Test
    void testProducerFactoryConfiguration() {
        // Arrange
        KafkaConfig kafkaConfig = new KafkaConfig();

        // Act
        ProducerFactory<String, String> producerFactory = kafkaConfig.producerFactory();

        // Assert
        assertNotNull(producerFactory);
        assertTrue(producerFactory instanceof DefaultKafkaProducerFactory);

        DefaultKafkaProducerFactory<?, ?> defaultFactory = (DefaultKafkaProducerFactory<?, ?>) producerFactory;
        Map<String, Object> configProps = defaultFactory.getConfigurationProperties();

        assertEquals("localhost:9092", configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(52428800, configProps.get(ProducerConfig.BATCH_SIZE_CONFIG));
        assertEquals(52428800, configProps.get(ProducerConfig.BUFFER_MEMORY_CONFIG));
        assertEquals(52428800, configProps.get(ProducerConfig.MAX_REQUEST_SIZE_CONFIG));
    }

    @Test
    void testKafkaTemplateConfiguration() {
        // Arrange
        KafkaConfig kafkaConfig = new KafkaConfig();

        // Act
        KafkaTemplate<String, String> kafkaTemplate = kafkaConfig.kafkaTemplate();

        // Assert
        assertNotNull(kafkaTemplate);
        assertNotNull(kafkaTemplate.getProducerFactory());
        // Verifica se a factory do template é do mesmo tipo que a factory criada diretamente
        assertEquals(kafkaConfig.producerFactory().getClass(), kafkaTemplate.getProducerFactory().getClass());

        // Verifica as configurações são as mesmas
        DefaultKafkaProducerFactory<?, ?> templateFactory =
                (DefaultKafkaProducerFactory<?, ?>) kafkaTemplate.getProducerFactory();
        DefaultKafkaProducerFactory<?, ?> directFactory =
                (DefaultKafkaProducerFactory<?, ?>) kafkaConfig.producerFactory();

        assertEquals(
                directFactory.getConfigurationProperties(),
                templateFactory.getConfigurationProperties()
        );
    }
}