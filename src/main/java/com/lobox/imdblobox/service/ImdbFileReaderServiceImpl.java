package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.BasicDto;
import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;
import com.lobox.imdblobox.controller.dto.PrincipalDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImdbFileReaderServiceImpl implements ImdbFileReaderService {
    private static final long LIMIT = 2000000;
    private static final short HEADER_INDEX = 1;

    @Override
    public Set<CrewDto> readAllCrewListFromFile() throws IOException {
        BufferedInputStream inputStream = (BufferedInputStream) ImbdService.class.getResourceAsStream("/home/saeidkazemi/title.crew.tsv");
        Set<CrewDto> crewDtoList;
        assert inputStream != null;
        //This code snippet does not bring the whole file in memory at once.
        //Using try-with-resources to close BufferedReader automatically
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            crewDtoList = br.lines().skip(HEADER_INDEX).limit(LIMIT).map(mapValue -> {
                String[] fields = mapValue.split("\t");
                CrewDto crewDto = new CrewDto();
                crewDto.setTConst(fields[0]);
                crewDto.setDirector(fields[1]);
                crewDto.setWriter(fields[2].replace("\\N", ""));
                return crewDto;
            }).collect(Collectors.toSet());
        }
        return crewDtoList;
    }

    @Override
    public Set<CrewDto> readAllCrewListWithSameDirectorAndWriter() throws IOException {
        File file = new File("/home/saeidkazemi/title.crew.tsv");
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream inputStream = new BufferedInputStream(fis)) {
            Set<CrewDto> crewDtoList;
            //This code snippet does not bring the whole file in memory at once.
            //Using try-with-resources to close BufferedReader automatically
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                crewDtoList = br.lines().skip(HEADER_INDEX).limit(LIMIT).map(mapValue -> {
                    return mapValue.split("\t");
                }).filter(fields -> !fields[1].equals("\\N") && fields[1].equals(fields[2])).map(mapValue -> {
                    CrewDto crewDto = new CrewDto();
                    crewDto.setTConst(mapValue[0]);
                    crewDto.setDirector(mapValue[1]);
                    crewDto.setWriter(mapValue[2].replace("\\N", ""));
                    return crewDto;
                }).collect(Collectors.toSet());
            }
            return crewDtoList;
        }
    }

    @Override
    public Set<BasicDto> readBasicListFromFile() throws IOException {
        File file = new File("/home/saeidkazemi/title.basics.tsv");
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream inputStream = new BufferedInputStream(fis)) {
            Set<BasicDto> basicDtosList;
            //This code snippet does not bring the whole file in memory at once.
            //Using try-with-resources to close BufferedReader automatically
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                basicDtosList = br.lines().skip(HEADER_INDEX).limit(LIMIT).map(mapValue -> {
                    String[] fields = mapValue.split("\t");
                    BasicDto basicDto = new BasicDto();
                    basicDto.setTconst(fields[0]);
                    basicDto.setTitleType(fields[1]);
                    basicDto.setPrimaryTitle(fields[2]);
                    basicDto.setOriginalTitle(fields[3]);
                    basicDto.setIsAdult(fields.length > 4 ? fields[3] : "");
                    basicDto.setStartYear(fields.length > 5 ? fields[5] : "");
                    basicDto.setEndYear(fields.length > 6 ? fields[6] : "");
                    basicDto.setRuntimeMinutes(fields.length > 7 ? fields[7] : "");
                    basicDto.setGenres(fields.length > 8 ? fields[8] : "");
                    return basicDto;
                }).collect(Collectors.toSet());
            }
            return basicDtosList;
        }
    }

    @Override
    public Set<PrincipalDto> readPrincipalsListFromFile() throws IOException {
        BufferedInputStream inputStream = (BufferedInputStream) ImbdService.class.getResourceAsStream("/home/saeidkazemi/title.principals.tsv");
        Set<PrincipalDto> principalDto;
        assert inputStream != null;
        //This code snippet does not bring the whole file in memory at once.
        //Using try-with-resources to close BufferedReader automatically
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            principalDto = br.lines().skip(HEADER_INDEX).limit(LIMIT).map(mapValue -> {
                String[] fields = mapValue.split("\t");
                PrincipalDto principalObj = new PrincipalDto();
                principalObj.setTconst(fields[0]);
                principalObj.setOrdering(String.valueOf(fields[1]));
                principalObj.setNconst(fields[2]);
                principalObj.setCategory(fields[3]);
                principalObj.setJob(fields[4].replace("\\N", ""));
                principalObj.setCharacters(fields[5].replace("\\N", ""));
                return principalObj;
            }).collect(Collectors.toSet());
        }
        return principalDto;
    }

    @Override
    public Set<NameBasicDto> readNameBasicListFromFile() throws IOException {
        BufferedInputStream inputStream = (BufferedInputStream) ImbdService.class.getResourceAsStream("/home/saeidkazemi/name.basics.tsv");
        Set<NameBasicDto> nameBasicDtoList;
        assert inputStream != null;
        //This code snippet does not bring the whole file in memory at once.
        //Using try-with-resources to close BufferedReader automatically
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            nameBasicDtoList = br.lines().skip(HEADER_INDEX).limit(LIMIT).map(mapValue -> {
                String[] fields = mapValue.split("\t");
                NameBasicDto nameBasicDto = new NameBasicDto();
                nameBasicDto.setNConst(fields[0]);
                nameBasicDto.setPrimaryName(fields[1]);
                nameBasicDto.setBirthYear(fields[2].replaceAll("/N", "0"));
                nameBasicDto.setDeathYear(fields[3].replaceAll("/N", "0"));
                nameBasicDto.setPrimaryProfession(fields[4]);
                nameBasicDto.setKnownForTitles(fields[5]);
                return nameBasicDto;
            }).collect(Collectors.toSet());
        }
        return nameBasicDtoList;
    }

    @Override
    public Set<NameBasicDto> readAliveNameBasicListFromFile() throws IOException {
        File file = new File("/home/saeidkazemi/name.basics.tsv");
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream inputStream = new BufferedInputStream(fis)) {
            Set<NameBasicDto> nameBasicDtoList = null;
            //This code snippet does not bring the whole file in memory at once.
            //Using try-with-resources to close BufferedReader automatically
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                try {
                    nameBasicDtoList = br.lines().skip(HEADER_INDEX).limit(LIMIT).map(mapValue -> {
                        return mapValue.split("\t");
                    }).filter(fields -> (fields.length > 3) && (fields[3].equals("\\N") || ObjectUtils.isEmpty(fields[3]))).map(mapValue -> {
                        NameBasicDto nameBasicDto = new NameBasicDto();
                        nameBasicDto.setNConst(mapValue[0]);
                        nameBasicDto.setPrimaryName(mapValue[1]);
                        nameBasicDto.setBirthYear(mapValue[2].replaceAll("/N", ""));
                        nameBasicDto.setDeathYear(mapValue[3].replaceAll("/N", ""));
                        nameBasicDto.setPrimaryProfession(mapValue[4]);
                        nameBasicDto.setKnownForTitles(mapValue[5]);
                        return nameBasicDto;
                    }).collect(Collectors.toSet());
                } catch (Exception e) {
                    log.error("Exception : ", e);
                }
                return nameBasicDtoList;
            }
        }
    }
}
