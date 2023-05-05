package com.solvd.noteservice.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class NoteDto {

    private Long id;

    @NotNull(message = "Description is required")
    @Length(max = 200, message = "Description must be shorter than 200 characters")
    private String description;

    @NotNull(message = "Theme is required")
    @Length(max = 100, message = "Theme must be shorter than 100 characters")
    private String theme;

    @NotNull(message = "Tag is required")
    @Length(max = 100, message = "Tag must be shorter than 100 characters")
    private String tag;

    @NotNull(message = "User id is required")
    private Long userId;

}
