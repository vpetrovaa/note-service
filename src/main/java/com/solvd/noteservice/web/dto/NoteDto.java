package com.solvd.noteservice.web.dto;

import com.solvd.noteservice.web.dto.util.ConstraintsUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class NoteDto {

    private Long id;

    @NotNull(message = "Description is required")
    @Length(max = ConstraintsUtils.DESCRIPTION_MAX_SIZE,
            message = "Description must be shorter than 200 characters")
    private String description;

    @NotNull(message = "Theme is required")
    @Length(max = ConstraintsUtils.THEME_MAX_SIZE,
            message = "Theme must be shorter than 100 characters")
    private String theme;

    @NotNull(message = "Tag is required")
    @Length(max = ConstraintsUtils.TAG_MAX_SIZE,
            message = "Tag must be shorter than 100 characters")
    private String tag;

    @NotNull(message = "User id is required")
    private Long userId;

}
