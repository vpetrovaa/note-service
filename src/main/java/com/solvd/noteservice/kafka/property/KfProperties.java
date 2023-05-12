package com.solvd.noteservice.kafka.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@Getter
@ConfigurationProperties(prefix = "kafka")
public class KfProperties {

    private final String topic;
    private final Integer partitions;
    private final Integer replicas;
    private final String port;
    private final String key;

}
