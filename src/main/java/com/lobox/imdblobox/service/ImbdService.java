package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.BasicDto;
import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;
import com.lobox.imdblobox.controller.dto.RatingDto;
import com.lobox.imdblobox.controller.dto.RequestCountDto;
import com.lobox.imdblobox.interceptor.RequestCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImbdService {

    private final ImdbFileReaderService imdbFileReaderService;
    private final RequestCounter requestCounter;

    public ImbdService(ImdbFileReaderService imdbFileReaderService, RequestCounter requestCounter) {
        this.imdbFileReaderService = imdbFileReaderService;
        this.requestCounter = requestCounter;
    }

    /**
     * Return all the titles in which both director and writer are the same person and he/she is still alive
     */
    public Set<String> readTitlesWithSameDirectorAndWriter() throws IOException, InterruptedException, ExecutionException {
        Set<String> titleIds = new HashSet<>();
        Set<CrewDto> crewDtoList = imdbFileReaderService.readAllCrewListWithSameDirectorAndWriter();
        Set<String> directorIds = crewDtoList.stream()
                .map(CrewDto::getDirector)
                .collect(Collectors.toSet());
        Set<String> aliveNameBasicList = imdbFileReaderService.readNameBasicListFromFile(true).stream().map(NameBasicDto::getNConst).collect(Collectors.toSet());
        for (String nConst : aliveNameBasicList) {
            if (directorIds.contains(nConst)) {
                titleIds.addAll(crewDtoList.stream()
                        .filter(crew -> crew.getDirector().equals(nConst))
                        .map(CrewDto::getTConst)
                        .collect(Collectors.toSet()));

            }
        }
        Set<BasicDto> basicList = null;
        basicList = imdbFileReaderService.readBasicListFromFile();
        return basicList.stream()
                .filter(value -> titleIds.contains(value.getTconst()))
                .map(BasicDto::getPrimaryTitle)
                .collect(Collectors.toSet());
    }

    /**
     * Get two actors and return all the titles in which both of them played at
     */
    public Set<String> readTitlesThatActorsPlayedAt(String firstActorId, String secondActorId) throws IOException {
        Set<String> titleIds;
        String firstActorTitleString = imdbFileReaderService.readActorInfo(firstActorId).getKnownForTitles();
        Set<String> firstActorTitleIds = new HashSet<>(Arrays.asList(firstActorTitleString.split(",")));
        String secondActorTitleString = imdbFileReaderService.readActorInfo(secondActorId).getKnownForTitles();
        Set<String> secondActorTitleIds = new HashSet<>(Arrays.asList(secondActorTitleString.split(",")));
        titleIds = firstActorTitleIds.stream().filter(secondActorTitleIds::contains).collect(Collectors.toSet());
        if (titleIds.isEmpty()) {
            return Collections.emptySet();
        } else {
            return imdbFileReaderService.readBasicListFromFileBasedOnList(titleIds)
                    .stream().map(BasicDto::getPrimaryTitle).collect(Collectors.toSet());
        }
    }


    /**
     * Get a genre from the user and return best titles on each year for that genre based on number of votes
     */
    public Map<String, String> readTitlesBasedOnGenreAndVote(String genre) throws IOException {
        Set<BasicDto> filmListTitlesBasedOnGenre = imdbFileReaderService.readTitlesBasedOnGenre(genre);
        Set<String> titleIds = filmListTitlesBasedOnGenre.stream().map(BasicDto::getTconst).collect(Collectors.toSet());
        if (titleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<RatingDto> relatedRatings = imdbFileReaderService.readRatingListBasedOnTitleIds(titleIds);
        ConcurrentMap<String, String> yearToTitleMap = filmListTitlesBasedOnGenre.parallelStream()
                .collect(Collectors.groupingByConcurrent(BasicDto::getStartYear, ConcurrentHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(basicDto -> {
                                    RatingDto relatedRating = relatedRatings.parallelStream()
                                            .filter(ratingDto -> ratingDto.getTconst().equals(basicDto.getTconst()))
                                            .findFirst()
                                            .orElse(null);
                                    return relatedRating != null ? Float.parseFloat(relatedRating.getNumVotes()) : 0;
                                })),
                                maxRatingDto -> maxRatingDto.isPresent() ?
                                        maxRatingDto.get().getPrimaryTitle() :
                                        ""
                        )));
        return yearToTitleMap;
    }

    /**
     * Count how many HTTP requests you received in this application since the last
     */
    public RequestCountDto readTotalRequestCount() {
        return new RequestCountDto(requestCounter.getTotalRequestCount());
    }

}
