package com.solvd.noteservice.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class NoteDto {

    @NotNull(message = "Id is required")
    private Long id;

    @NotNull(message = "Description is required")
    @Length(max = 200, message = "Description must be shorter than 200 characters")
    private String description;

    @NotNull(message = "User id is required")
    private Long userId;

}
