package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.CrewDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImbdService {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);


    public static void main(String[] args) throws IOException {


        List<CrewDto> crewDtoList = readCrewListFromFile();

    }

    /**
     This code does not bring the whole file in memory at once.
     */
    public static List<CrewDto> readCrewListFromFile() throws IOException {
        InputStream inputStream = ImbdService.class.getResourceAsStream("/dataset/data.tsv");
        List<CrewDto> crewDtoList = new ArrayList<>();
            assert inputStream != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                crewDtoList = br.lines().limit(60000).map(mapValue -> {
                    String[] fields = mapValue.split("\t");
                    String tConst = fields[0];
                    String director = fields[1];
                    String writer = fields[2];
                    CrewDto crewDto = new CrewDto();
                    crewDto.setTConst(tConst);
                    crewDto.setDirector(director);
                    crewDto.setWriter(writer);
                    return crewDto;
                }).toList();
            }
        return crewDtoList;
    }



}
