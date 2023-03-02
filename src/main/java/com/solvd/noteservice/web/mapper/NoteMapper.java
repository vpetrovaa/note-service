package com.solvd.noteservice.web.mapper;

import com.solvd.noteservice.domain.Note;
import com.solvd.noteservice.web.dto.NoteDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteDto toDto(Note note);

    Note toEntity(NoteDto noteDto);

    List<NoteDto> toDtoList(List<Note> notes);
}
