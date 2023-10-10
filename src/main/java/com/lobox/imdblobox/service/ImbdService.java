package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.BasicDto;
import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;
import org.springframework.data.util.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ImbdService {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static final ImdbFileReaderServiceImpl imdbFileReaderService = new ImdbFileReaderServiceImpl();

    /*
        public ImbdService(ImdbFileReaderService imdbFileReaderService) {
            this.imdbFileReaderService = imdbFileReaderService;
        }
    */
    private static final int CHUNK_SIZE = 100000;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        Set<String> listData2 = readAliveSameDirectorAndWriterTitles2();
        Set<String> listData3 = readAliveSameDirectorAndWriterTitlesSingleThread();

        long end = System.currentTimeMillis();
        //43 second last seconds
        //30 second to execute overall process
        System.out.println((end - start) + " millisecond");
    }





















    public static Set<String> readAliveSameDirectorAndWriterTitles2() throws IOException, InterruptedException, ExecutionException {
        CountDownLatch latch = new CountDownLatch(1);

        Set<String> titleIds = new HashSet<>();
        AtomicReference<Set<CrewDto>> crewDtoList = new AtomicReference<>(new HashSet<>());
        AtomicReference<Set<String>> finalTitles = new AtomicReference<>(new HashSet<>());
        Thread thread1 = new Thread(() -> {
            try {
                crewDtoList.set(imdbFileReaderService.readAllCrewListWithSameDirectorAndWriter());
                latch.countDown();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread2 = new Thread(() -> {
            // Wait for Thread 1 to complete its work
            try {
                latch.await();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Set<String> directorIds = crewDtoList.get().stream()
                    .map(CrewDto::getDirector)
                    .collect(Collectors.toSet());

            Set<String> aliveNameBasicList = null;
            try {
                aliveNameBasicList = imdbFileReaderService.readAliveNameBasicListFromFile().stream().map(NameBasicDto::getNConst).collect(Collectors.toSet());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (String nConst : aliveNameBasicList) {
                if (directorIds.contains(nConst)) {
                    titleIds.addAll(crewDtoList.get().stream()
                            .filter(crew -> crew.getDirector().equals(nConst))
                            .map(CrewDto::getTConst)
                            .collect(Collectors.toSet()));

                }
            }

            Set<BasicDto> basicList = null;
            try {
                basicList = imdbFileReaderService.readBasicListFromFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finalTitles.set(basicList.stream()
                    .filter(value -> titleIds.contains(value.getTconst()))
                    .map(BasicDto::getPrimaryTitle)
                    .collect(Collectors.toSet()));

        });
        thread1.start();
        thread2.start();
        thread2.join();
        return finalTitles.get();
    }

    public static Set<String> readAliveSameDirectorAndWriterTitlesSingleThread() throws IOException, InterruptedException, ExecutionException {
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
        Set<BasicDto> basicList = null;
        basicList = imdbFileReaderService.readBasicListFromFile();
        return basicList.stream()
                .filter(value -> titleIds.contains(value.getTconst()))
                .map(BasicDto::getPrimaryTitle)
                .collect(Collectors.toSet());
    }










/*    File file = new File("/home/saeidkazemi/title.crew.tsv");
        try (
    FileInputStream fis = new FileInputStream(file); BufferedInputStream inputStream = new BufferedInputStream(fis)) {
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
    }*/

    public static Set<String> readAliveSameDirectorAndWriterTitles() throws IOException, InterruptedException, ExecutionException {
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
                .map(BasicDto::getPrimaryTitle).collect(Collectors.toSet());


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
