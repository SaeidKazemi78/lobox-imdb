package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.CrewDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ImbdService {





    public static void readData(){


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<CrewDto> crewDtoList = readCrewListFromFile();

            }
        }) ;
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                List<CrewDto> crewDtoList = readCrewListFromFile();

            }
        }) ;
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                List<CrewDto> crewDtoList = readCrewListFromFile();

            }
        }) ;
        thread.start();
        thread2.start();
        thread3.start();


    }


    public static List<CrewDto> readCrewListFromFile() {
        InputStream inputStream = ImbdService.class.getResourceAsStream("/dataset/data.tsv");
        List<CrewDto> crewDtoList = new ArrayList<>();
        try {
            assert inputStream != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                crewDtoList = br.lines().limit(00).map(mapValue -> {
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
        } catch (IOException e) {
            //Handle exception
            e.printStackTrace();
        }

        return crewDtoList;
    }

    public static void main(String[] args) {

        readCrewListFromFile();

    }

}
