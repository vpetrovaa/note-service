package com.solvd.noteservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteEvent {

    private Method type;
    private Long id;
    private String description;
    private String theme;
    private String tag;
    private Long userId;

    public enum Method {
        UPDATE, DELETE, SAVE;
    }

}
