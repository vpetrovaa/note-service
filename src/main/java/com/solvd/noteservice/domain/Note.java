package com.solvd.noteservice.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notes")
@Data
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String theme;

    private String tag;

    @Column(name = "user_id")
    private Long userId;

}
