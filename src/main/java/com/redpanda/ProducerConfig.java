package com.redpanda;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@Configuration
public class ProducerConfig {
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${redpanda.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", KafkaAvroSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, NasdaqHistorical> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, NasdaqHistorical> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public Producer sender() {
        return new Producer();
    }
}
