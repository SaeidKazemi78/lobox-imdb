package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.BasicDto;
import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImbdService {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static final ImdbFileReaderServiceImpl imdbFileReaderService = new ImdbFileReaderServiceImpl();

    /*
        public ImbdService(ImdbFileReaderService imdbFileReaderService) {
            this.imdbFileReaderService = imdbFileReaderService;
        }
    */

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
//        List<String> listData2 = readAliveSameDirectorAndWriterTitles();
        Set<String> listData2 = readAliveSameDirectorAndWriterTitles2();
//        listData.forEach(System.out::println);
        long end = System.currentTimeMillis();
        //43 second last seconds
        System.out.println((end - start) + " millisecond");
    }

    public static Set<String> readAliveSameDirectorAndWriterTitles2() throws IOException, InterruptedException, ExecutionException {

        Set<String> titleIds = new HashSet<>();
        Set<CrewDto> crewDtoList = imdbFileReaderService.readAllCrewListWithSameDirectorAndWriter();
        Set<String> directorIds = crewDtoList.stream()
                .map(CrewDto::getDirector)
                .collect(Collectors.toSet());
        Set<String> aliveNameBasicList = imdbFileReaderService.readAliveNameBasicListFromFile().stream().map(NameBasicDto::getNConst).collect(Collectors.toSet());
        for (String nConst : aliveNameBasicList) {
            if (directorIds.contains(nConst)) {
                titleIds.addAll(crewDtoList.stream()
                        .filter(crew -> crew.getDirector().equals(nConst))
                        .map(CrewDto::getTConst)
                        .collect(Collectors.toSet()));
            }
        }
        Set<BasicDto> basicList = imdbFileReaderService.readBasicListFromFile();
        return basicList.stream()
                .filter(value -> titleIds.contains(value.getTconst()))
                .map(BasicDto::getPrimaryTitle)
                .collect(Collectors.toSet());

    }


    public static List<String> readAliveSameDirectorAndWriterTitles() throws IOException, InterruptedException, ExecutionException {
        Set<String> titleIds = new HashSet<>();
        Set<CrewDto> crewDtoLis = imdbFileReaderService.readAllCrewListWithSameDirectorAndWriter();
        imdbFileReaderService.readAliveNameBasicListFromFile().stream().
                filter(filterValue -> crewDtoLis.stream()
                        .peek(mapValue -> {
                            if (mapValue.getDirector().equals(filterValue.getNConst())) {
                                titleIds.add(mapValue.getTConst());
                            }
                        })
                        .anyMatch((crew) -> {
                                    return crew.getDirector().equals(filterValue.getNConst());
                                }
                        )).collect(Collectors.toSet());
        return imdbFileReaderService.readBasicListFromFile().stream().filter(value -> titleIds.stream().anyMatch(matchValue -> matchValue.equals(value.getTconst())))
                .map(BasicDto::getPrimaryTitle).toList();


/*      BlockingQueue<List<CrewDto>> crewDtoListQueue = new LinkedBlockingQueue<>();

        executorService.submit(() -> {
            try {
                List<CrewDto> crewDtoLis = imdbFileReaderService.readAllCrewListWithSameDirectorAndWriter();
                crewDtoListQueue.put(crewDtoLis);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

      List<String> titleIds = new ArrayList<>();


          executorService.submit(() -> {
            try {
                List<CrewDto> crewDtoListStream = crewDtoListQueue.take();

                List<NameBasicDto> finalList =   imdbFileReaderService.readAliveNameBasicListFromFile().stream().
                        filter(filterValue ->crewDtoListStream.stream().anyMatch(crew->
                                crew.getDirector().equals(filterValue.getNConst()))).toList();


                titleIds.addAll(finalList.stream().map(NameBasicDto::getKnownForTitles).toList());

*//*
                imdbFileReaderService.readBasicListFromFile().stream()
//                        .stream().filter(filter -> titleIds.contains(filter.getTconst()))
                        .map(BasicDto::getPrimaryTitle)
                        .collect(Collectors.toList());

                System.out.println();
*//*

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Future<List<String>> finalPrimaryTitlesFuture = executorService.submit(() -> {
            //                imdbFileReaderService.readBasicListFromFile();
            return new ArrayList<>();
*//*                return imdbFileReaderService.readBasicListFromFile().stream()
//                        .stream().filter(filter -> titleIds.contains(filter.getTconst()))
                        .map(BasicDto::getPrimaryTitle)
                        .collect(Collectors.toList());*//*
        });

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        return finalPrimaryTitlesFuture.get();
*/


    }
}
