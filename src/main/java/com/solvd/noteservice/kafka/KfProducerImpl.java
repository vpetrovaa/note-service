package com.solvd.noteservice.kafka;

import com.solvd.noteservice.kafka.event.NoteEvent;
import com.solvd.noteservice.kafka.property.KfProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KfProducerImpl implements KfProducer {

    private final KafkaTemplate<String, NoteEvent> kafkaTemplate;
    private final KfProperties kfProperties;

    @Override
    public final void sendMessage(final NoteEvent event) {
        kafkaTemplate.send(kfProperties.getTopic(), event);
    }

}
