package com.redpanda;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.StringUtils;

public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, NasdaqHistorical> kafkaTemplate;

    public void send(NasdaqHistorical payload) {
        CompletableFuture<SendResult<String, NasdaqHistorical>> future = kafkaTemplate.send(topic, payload);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent record at offset {}: '{}'", 
                    result.getRecordMetadata().offset(),
                    StringUtils.truncate(payload.toString())
                );
            }
            else {
                log.error("Error sending record: {}", ex.toString());
            }
        });
    }
}
