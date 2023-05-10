package com.solvd.noteservice.kafka;

import com.solvd.noteservice.kafka.event.NoteEvent;

public interface KfProducer {

    void sendMessage(NoteEvent event);

}
