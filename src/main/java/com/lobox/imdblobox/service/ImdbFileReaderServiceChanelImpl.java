package com.lobox.imdblobox.service;

import com.lobox.imdblobox.controller.dto.BasicDto;
import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;
import com.lobox.imdblobox.controller.dto.PrincipalDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class ImdbFileReaderServiceChanelImpl implements ImdbFileReaderService {


    public static void main(String[] args) throws Exception {
        String filePath = "/home/saeidkazemi/title.crew.tsv"; // Replace with your file path
        int numThreads = Runtime.getRuntime().availableProcessors(); // Use available CPU cores
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        // Open the file using a RandomAccessFile and map it into memory
        String READ_MODE = "r";
        try (RandomAccessFile file = new RandomAccessFile(filePath, READ_MODE);
             FileChannel channel = file.getChannel()) {

            long fileSize = channel.size();
            int chunkSize = (int) Math.ceil((double) fileSize / numThreads);
            // Create tasks for parallel processing
            AtomicLong totalProcessed = new AtomicLong(0);
            List<CrewDto> crewDtoList = new ArrayList<>();
            for (int i = 0; i < numThreads; i++) {
                long startPosition = (long) i * chunkSize;
                long endPosition = Math.min((long) (i + 1) * chunkSize, fileSize);
                executor.submit(() -> {
                    try {
                        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, startPosition, endPosition - startPosition);
                        List<String> lines = readLines(buffer, StandardCharsets.UTF_8);
                        crewDtoList.addAll(lines.stream().map(mapValue -> {
                            return mapValue.split("\t");
//                        }).filter(fields -> !fields[1].equals("\\N") && fields[1].equals(fields[2])).map(mapValue -> {
                        }).map(mapValue -> {
                            CrewDto crewDto = new CrewDto();
                            crewDto.setTConst(mapValue.length > 0 ? mapValue[0]:"");
                            crewDto.setDirector(mapValue.length > 1 ? mapValue[1] : "");
                            crewDto.setWriter(mapValue.length > 2 ? mapValue[2].replace("\\N", "") : "");
                            return crewDto;
                        }).toList());
                        // Update totalProcessed with the number of bytes processed in this chunk
                        totalProcessed.addAndGet(endPosition - startPosition);
                    } catch (Exception e) {
                        log.error("Exception :", e);
                    }

                });

            }

            /**
             * call the shutdown() method on the ExecutorService to indicate that you're not going to submit any more tasks.
             */
            executor.shutdown();

            try {
                // Wait for all tasks to complete or until a timeout (e.g., 10 seconds)
                if (!executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                    // If the timeout is reached and tasks are not done, you can take some action
                    System.out.println("Some tasks are still running after the timeout.");
                }
            } catch (InterruptedException e) {
                // Handle interrupted exception
                e.printStackTrace();
                System.out.println("interrupted exception");
            }
            log.info("\n All tasks have completed.");


        }
    }
/*    private static void processChunk(FileChannel channel, long startPosition, long endPosition, AtomicLong totalProcessed) {

    }*/

    private static List<String> readLines(MappedByteBuffer buffer, Charset charset) {
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

    @Override
    public Set<CrewDto> readAllCrewListFromFile() throws IOException {
        return null;
    }

    @Override
    public Set<CrewDto> readAllCrewListWithSameDirectorAndWriter() throws IOException {
        return null;
    }

    /**
     * br.lines().skip(1).limit(LIMIT).map(mapValue -> {
     * return mapValue.split("\t");
     * }).filter(fields -> !fields[1].equals("\\N") && fields[1].equals(fields[2])).map(mapValue -> {
     * CrewDto crewDto = new CrewDto();
     * crewDto.setTConst(mapValue[0]);
     * crewDto.setDirector(mapValue[1]);
     * crewDto.setWriter(mapValue[2].replace("\\N", ""));
     * return crewDto;
     * }).collect(Collectors.toSet());
     *
     * @return
     * @throws IOException
     */

    @Override
    public Set<BasicDto> readBasicListFromFile() throws IOException {
        return null;
    }

    @Override
    public Set<PrincipalDto> readPrincipalsListFromFile() throws IOException {
        return null;
    }

    @Override
    public Set<NameBasicDto> readNameBasicListFromFile() throws IOException {
        return null;
    }

    @Override
    public Set<NameBasicDto> readAliveNameBasicListFromFile() throws IOException {
        return null;
    }
}
