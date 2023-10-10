package com.lobox.imdblobox.controller;

import com.lobox.imdblobox.controller.dto.RequestCountDto;
import com.lobox.imdblobox.service.ImbdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v0.1/imdb")
public class ImbdController {

    private final ImbdService imbdService;

    public ImbdController(ImbdService imbdService) {
        this.imbdService = imbdService;
    }

    @GetMapping("/read-same-director-and-writer")
    public ResponseEntity<Set<String>> readTitlesWithSameDirectorAndWriter() throws Exception {
        return ResponseEntity.ok(imbdService.readTitlesWithSameDirectorAndWriter());
    }

    @GetMapping("/read-based-on-actors")
    public ResponseEntity<Set<String>> readTitlesThatActorsPlayedAt(@RequestParam String firstActorId, @RequestParam String secondActorId) throws Exception {
        return ResponseEntity.ok(imbdService.readTitlesThatActorsPlayedAt(firstActorId, secondActorId));
    }

    @GetMapping("/read-based-on-genre")
    public ResponseEntity<Map<String, String>> readTitlesBasedOnGenreAndVote(@RequestParam String genre) throws Exception {
        return ResponseEntity.ok(imbdService.readTitlesBasedOnGenreAndVote(genre));
    }

    @GetMapping("/read-total-request-count")
    public ResponseEntity<RequestCountDto> readTotalRequestCount() {
        return ResponseEntity.ok(imbdService.readTotalRequestCount());
    }

}
