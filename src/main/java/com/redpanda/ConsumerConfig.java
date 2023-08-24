package com.redpanda;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

@Configuration
@EnableKafka
public class ConsumerConfig {
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${redpanda.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", KafkaAvroDeserializer.class);
        props.put("specific.avro.reader", "true");
        props.put("group.id", "test");
        props.put("auto.offset.reset", "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, NasdaqHistorical> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, NasdaqHistorical>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NasdaqHistorical> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public Consumer consumer() {
        return new Consumer();
    }
}
