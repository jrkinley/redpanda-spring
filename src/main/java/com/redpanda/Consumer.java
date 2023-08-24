package com.redpanda;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.StringUtils;

public class Consumer {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "${kafka.topic}")
    public void receive(ConsumerRecord<String, NasdaqHistorical> record) {
        log.info("Received record: '{}'", StringUtils.truncate(record.value().toString()));
    }
}
