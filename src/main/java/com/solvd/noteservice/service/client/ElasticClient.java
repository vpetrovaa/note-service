package com.solvd.noteservice.service.client;

import com.solvd.noteservice.domain.Note;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "esearch", url = "http://${open.feign.host}/api/v1/esearch")
public interface ElasticClient {

    @GetMapping("/{id}")
    Optional<Note> findById(@PathVariable Long id);

    @GetMapping("/users/{userId}")
    List<Note> findAllByUserId(@PathVariable Long userId);

    @GetMapping
    List<Note> findAll();

    @GetMapping("/exists/{id}")
    boolean isExistById(@PathVariable Long id);

}
