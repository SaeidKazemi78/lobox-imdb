package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.BasicDto;
import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;
import com.lobox.imdblobox.controller.dto.RatingDto;

import java.io.IOException;
import java.util.Set;

public interface ImdbFileReaderService {
    Set<CrewDto> readAllCrewListWithSameDirectorAndWriter() throws IOException;

    Set<BasicDto> readBasicListFromFile() throws IOException;

    Set<NameBasicDto> readNameBasicListFromFile(boolean justAliveOnes) throws IOException;

    NameBasicDto readActorInfo(String actorId) throws IOException;

    Set<RatingDto> readRatingListBasedOnTitleIds(Set<String> titleIds) throws IOException;

    Set<BasicDto> readTitlesBasedOnGenre(String genre) throws IOException;

    Set<BasicDto> readBasicListFromFileBasedOnList(Set<String> actorIds) throws IOException;
}

