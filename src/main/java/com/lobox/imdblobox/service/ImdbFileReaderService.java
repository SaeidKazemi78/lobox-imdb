package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.BasicDto;
import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;
import com.lobox.imdblobox.controller.dto.PrincipalDto;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ImdbFileReaderService {

    Set<CrewDto> readAllCrewListFromFile() throws IOException;
    Set<CrewDto> readAllCrewListWithSameDirectorAndWriter() throws IOException;

    Set<BasicDto> readBasicListFromFile() throws IOException;

    Set<PrincipalDto> readPrincipalsListFromFile() throws IOException;


    Set<NameBasicDto> readNameBasicListFromFile() throws IOException;
    Set<NameBasicDto> readAliveNameBasicListFromFile() throws IOException;
}
