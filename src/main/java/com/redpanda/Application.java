package com.redpanda;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class Application {
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.topic}")
    private String topic;

    private final Producer producer;

    @Autowired
    Application(Producer producer) {
        this.producer = producer;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    private NasdaqHistorical Parse(String line) {
        String[] parts = line.split(",");
        return new NasdaqHistorical(
            parts[0],
            parts[1],
            parts[2],
            parts[3],
            parts[4],
            parts[5]);
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, NasdaqHistorical> kafkaTemplate) {
        return args -> {
            InputStream is = Application.class.getClassLoader().getResourceAsStream("HistoricalData_NVDA_5Y.csv");
            InputStreamReader sr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(sr);
            reader.readLine(); // Ignore header
            String line = reader.readLine();
            while (line != null) {
                this.producer.send(Parse(line));
                line = reader.readLine();
            }
            reader.close();
        };
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topic)
            .partitions(1)
            .replicas(1)
            .build();
    }
}
