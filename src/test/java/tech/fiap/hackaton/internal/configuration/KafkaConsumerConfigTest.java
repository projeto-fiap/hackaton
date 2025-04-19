package tech.fiap.hackaton.internal.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConsumerConfigTest {

    @Test
    void testConsumerFactoryConfiguration() {
        // Arrange
        KafkaConsumerConfig config = new KafkaConsumerConfig();

        // Act
        ConsumerFactory<String, String> consumerFactory = config.consumerFactory();

        // Assert
        assertNotNull(consumerFactory);
        assertTrue(consumerFactory instanceof DefaultKafkaConsumerFactory);

        DefaultKafkaConsumerFactory<?, ?> defaultFactory = (DefaultKafkaConsumerFactory<?, ?>) consumerFactory;
        Map<String, Object> configProps = defaultFactory.getConfigurationProperties();

        assertEquals("localhost:9092", configProps.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("video-service", configProps.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals("earliest", configProps.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));

        // Verificação por nome da classe para evitar conflitos de classloader
        assertEquals(
                org.apache.kafka.common.serialization.StringDeserializer.class.getName(),
                ((Class<?>) configProps.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)).getName()
        );
        assertEquals(
                org.apache.kafka.common.serialization.StringDeserializer.class.getName(),
                ((Class<?>) configProps.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)).getName()
        );
    }

    @Test
    void testKafkaListenerContainerFactoryConfiguration() {
        // Arrange
        KafkaConsumerConfig config = new KafkaConsumerConfig();

        // Act
        ConcurrentKafkaListenerContainerFactory<String, String> factory = config.kafkaListenerContainerFactory();

        // Assert
        assertNotNull(factory);
        assertNotNull(factory.getConsumerFactory());

        // Verifica se a factory do container usa a mesma consumer factory configurada
        ConsumerFactory<?, ?> containerConsumerFactory = factory.getConsumerFactory();
        ConsumerFactory<?, ?> directConsumerFactory = config.consumerFactory();

        assertEquals(directConsumerFactory.getClass(), containerConsumerFactory.getClass());

        // Verifica as configurações são as mesmas
        DefaultKafkaConsumerFactory<?, ?> containerFactory =
                (DefaultKafkaConsumerFactory<?, ?>) containerConsumerFactory;
        DefaultKafkaConsumerFactory<?, ?> directFactory =
                (DefaultKafkaConsumerFactory<?, ?>) directConsumerFactory;

        assertEquals(
                directFactory.getConfigurationProperties(),
                containerFactory.getConfigurationProperties()
        );
    }
}