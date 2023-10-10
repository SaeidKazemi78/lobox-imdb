package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("ImdbFileReaderServiceChanelImpl")
@Slf4j
public class ImdbFileReaderServiceChanelImpl implements ImdbFileReaderService {

    private final int PROCESSORS_COUNT = Runtime.getRuntime().availableProcessors();


    @Override
    public Set<CrewDto> readAllCrewListFromFile() throws IOException {

        return null;
    }

    @Override
    public Set<CrewDto> readAllCrewListWithSameDirectorAndWriter() throws IOException {
        String filePath = "/home/saeidkazemi/title.crew.tsv"; // Replace with your file path
        ExecutorService executor = Executors.newFixedThreadPool(PROCESSORS_COUNT);
        // Open the file using a RandomAccessFile and map it into memory
        String READ_MODE = "r";
        try (RandomAccessFile file = new RandomAccessFile(filePath, READ_MODE);
             FileChannel channel = file.getChannel()) {
            long fileSize = channel.size();
            int chunkSize = (int) Math.ceil((double) fileSize / PROCESSORS_COUNT);
            Set<CrewDto> crewDtoList = new HashSet<>();
            for (int i = 0; i < PROCESSORS_COUNT; i++) {
                long startPosition = (long) i * chunkSize;
                long endPosition = Math.min((long) (i + 1) * chunkSize, fileSize);
                executor.submit(() -> {
                    try {
                        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, startPosition, endPosition - startPosition);
                        List<String> lines = readLines(buffer);
                        crewDtoList.addAll(lines.stream().map(mapValue -> {
                            return mapValue.split("\t");
                        }).filter(fields ->
                                (fields.length > 1 && !fields[1].equals("\\N"))
                                        && (fields.length > 2 && fields[1].equals(fields[2])
                                )).map(mapValue -> {
                            CrewDto crewDto = new CrewDto();
                            crewDto.setTConst(mapValue[0]);
                            crewDto.setDirector(mapValue[1]);
                            crewDto.setWriter(mapValue[2].replace("\\N", ""));
                            return crewDto;
                        }).toList());

                    } catch (Exception e) {
                        log.error("Exception :", e);
                    }
                });

            }

            /**
             * call the shutdown() method on the ExecutorService to indicate that you're not going to submit any more tasks.
             */
            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);//We can do some tasks that should be run after timeout
            return crewDtoList;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public Set<BasicDto> readBasicListFromFileBasedOnList(Set<String> actorId) throws IOException {
        File file = new File("/home/saeidkazemi/name.basics.tsv");
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream inputStream = new BufferedInputStream(fis)) {
            Set<BasicDto> basicDtoList = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                try {
                    basicDtoList = br.lines().map(mapValue -> {
                        return mapValue.split("\t");
                    }).filter(filter -> actorId.contains(filter[0])).map(fields -> {
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
                } catch (Exception e) {
                    log.error("Exception : ", e);
                }
                return basicDtoList;
            }
        }
    }


    /**
     * Get All ratings based on titleIds
     */
    public Set<RatingDto> readRatingListBasedOnTitleIds(Set<String> titleIds) throws IOException {
        File file = new File("/home/saeidkazemi/title.ratings.tsv");
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream inputStream = new BufferedInputStream(fis)) {
            Set<RatingDto> ratingDtoSet = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                try {
                    ratingDtoSet = br.lines().map(mapValue -> {
                        return mapValue.split("\t");
                    }).filter(filter -> titleIds.contains(filter[0])).map(fields -> {
                        RatingDto basicDto = new RatingDto();
                        basicDto.setTconst(fields[0]);
                        basicDto.setAverageRating(fields[1]);
                        basicDto.setNumVotes(fields[2]);
                        return basicDto;
                    }).collect(Collectors.toSet());
                } catch (Exception e) {
                    log.error("Exception : ", e);
                }
                return ratingDtoSet;
            }
        }
    }

    @Override
    public Set<BasicDto> readBasicListFromFile() throws IOException {
        String filePath = "/home/saeidkazemi/title.basics.tsv"; // Replace with your file path
        ExecutorService executor = Executors.newFixedThreadPool(PROCESSORS_COUNT);
        // Open the file using a RandomAccessFile and map it into memory
        String READ_MODE = "r";
        try (RandomAccessFile file = new RandomAccessFile(filePath, READ_MODE);
             FileChannel channel = file.getChannel()) {
            long fileSize = channel.size();
            int chunkSize = (int) Math.ceil((double) fileSize / PROCESSORS_COUNT);
            // Create tasks for parallel processing
//            AtomicLong totalProcessed = new AtomicLong(0);
            Set<BasicDto> basicDtoList = new HashSet<>();
            for (int i = 0; i < PROCESSORS_COUNT; i++) {
                long startPosition = (long) i * chunkSize;
                long endPosition = Math.min((long) (i + 1) * chunkSize, fileSize);
                executor.submit(() -> {
                    MappedByteBuffer buffer = null;
                    try {
                        buffer = channel.map(FileChannel.MapMode.READ_ONLY, startPosition, endPosition - startPosition);
                    } catch (IOException e) {
                        //Handle exception
                        throw new RuntimeException(e);
                    }
                    List<String> lines = readLines(buffer);
                    basicDtoList.addAll(lines.stream().map(mapValue -> {
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
                    }).collect(Collectors.toSet()));
                });
            }
            /**
             * call the shutdown() method on the ExecutorService to indicate that you're not going to submit any more tasks.
             */
            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);//We can do some tasks that should be run after timeout
            return basicDtoList;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Set<PrincipalDto> readPrincipalsListFromFile() throws IOException {
        return null;
    }


    @Override
    public Set<NameBasicDto> readNameBasicListFromFile(boolean justAliveOnes) throws IOException {
        String filePath = "/home/saeidkazemi/name.basics.tsv"; // Replace with your file path
        ExecutorService executor = Executors.newFixedThreadPool(PROCESSORS_COUNT);
        // Open the file using a RandomAccessFile and map it into memory
        String READ_MODE = "r";
        try (RandomAccessFile file = new RandomAccessFile(filePath, READ_MODE);
             FileChannel channel = file.getChannel()) {
            long fileSize = channel.size();
            int chunkSize = (int) Math.ceil((double) fileSize / PROCESSORS_COUNT);
            // Create tasks for parallel processing
//            AtomicLong totalProcessed = new AtomicLong(0);
            Set<NameBasicDto> nameBasicDtos = new HashSet<>();
            for (int i = 0; i < PROCESSORS_COUNT; i++) {
                long startPosition = (long) i * chunkSize;
                long endPosition = Math.min((long) (i + 1) * chunkSize, fileSize);
                executor.submit(() -> {
                    MappedByteBuffer buffer = null;
                    try {
                        buffer = channel.map(FileChannel.MapMode.READ_ONLY, startPosition, endPosition - startPosition);
                    } catch (IOException e) {
                        //Handle exception
                        throw new RuntimeException(e);
                    }
                    List<String> lines = readLines(buffer);
                    nameBasicDtos.addAll(
                            lines.stream().map(mapValue -> {
                                return mapValue.split("\t");
                            }).filter(fields -> {
                                if (justAliveOnes) {
                                    return (fields.length > 3) && (fields[3].equals("\\N") || ObjectUtils.isEmpty(fields[3]));
                                } else {
                                    return true;
                                }
                            }).map(mapValue -> {
                                NameBasicDto nameBasicDto = new NameBasicDto();
                                nameBasicDto.setNConst(mapValue[0]);
                                nameBasicDto.setPrimaryName(mapValue[1]);
                                nameBasicDto.setBirthYear(mapValue[2].replaceAll("/N", ""));
                                nameBasicDto.setDeathYear(mapValue[3].replaceAll("/N", ""));
                                nameBasicDto.setPrimaryProfession(mapValue[4]);
                                nameBasicDto.setKnownForTitles(mapValue[5]);
                                return nameBasicDto;
                            }).collect(Collectors.toSet())
                    );
                });
            }
            /**
             * call the shutdown() method on the ExecutorService to indicate that you're not going to submit any more tasks.
             */
            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);//We can do some tasks that should be run after timeout
            return nameBasicDtos;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public NameBasicDto readActorInfo(String actorId) throws IOException {
        File file = new File("/home/saeidkazemi/name.basics.tsv");
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream inputStream = new BufferedInputStream(fis)) {
            NameBasicDto nameBasicDtoList = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                try {
                    nameBasicDtoList = br.lines().map(mapValue -> {
                        return mapValue.split("\t");
                    }).filter(fields -> (fields.length > 0) && fields[0].equals(actorId)).map(mapValue -> {
                        NameBasicDto nameBasicDto = new NameBasicDto();
                        nameBasicDto.setNConst(mapValue[0]);
                        nameBasicDto.setPrimaryName(mapValue[1]);
                        nameBasicDto.setBirthYear(mapValue[2].replaceAll("/N", ""));
                        nameBasicDto.setDeathYear(mapValue[3].replaceAll("/N", ""));
                        nameBasicDto.setPrimaryProfession(mapValue[4]);
                        nameBasicDto.setKnownForTitles(mapValue[5]);
                        return nameBasicDto;
                    }).findAny().orElse(null);
                } catch (Exception e) {
                    log.error("Exception : ", e);
                }
                return nameBasicDtoList;
            }
        }
    }

    private List<String> readLines(MappedByteBuffer buffer) {
        List<String> lines = new ArrayList<>();
        StringBuilder lineBuilder = new StringBuilder();
        while (buffer.hasRemaining()) {
            char c = (char) buffer.get();
            // Check for newline character ('\n') to detect line breaks
            if (c == '\n') {
                lines.add(lineBuilder.toString());
                lineBuilder.setLength(0); // Clear the StringBuilder for the next line
            } else {
                lineBuilder.append(c);
            }
        }
        // Add the last line if it doesn't end with a newline character
        if (!lineBuilder.isEmpty()) {
            lines.add(lineBuilder.toString());
        }
        return lines;
    }

}
