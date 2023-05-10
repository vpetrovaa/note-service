package com.solvd.noteservice.kafka.config;

import com.solvd.noteservice.kafka.event.NoteEvent;
import com.solvd.noteservice.kafka.property.KfProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KfProducerConfig {

    private final KfProperties kfProperties;

    @Bean
    public final NewTopic topic() {
        return TopicBuilder.name(kfProperties.getTopic())
                .partitions(kfProperties.getPartitions())
                .replicas(kfProperties.getReplicas())
                .build();
    }

    @Bean
    public final Map<String, Object> producerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kfProperties.getPort());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return config;
    }

    @Bean
    public final ProducerFactory<String, NoteEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public final KafkaTemplate<String, NoteEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
