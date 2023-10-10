package com.lobox.imdblobox;

import com.lobox.imdblobox.controller.dto.BasicDto;
import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;
import com.lobox.imdblobox.service.ImdbFileReaderServiceChanelImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class IMDBLoboxApplicationTests {

    @Autowired
    private ImdbFileReaderServiceChanelImpl imdbFileReaderService;


    @Test
    void readNameBasicListFromFileTest() throws IOException {
        long start = System.currentTimeMillis();
        Set<NameBasicDto> nameBasicDtoList = imdbFileReaderService.readNameBasicListFromFile(false);
        long end = System.currentTimeMillis();
        Assertions.assertThat(nameBasicDtoList).isNotEmpty();
        log.info("readNameBasicListFromFileTest Done in " + (end -start) + " millisecond");
    }

    @Test
    void readCrewListFromFileTest() throws IOException {
        long start = System.currentTimeMillis();
        Set<CrewDto> crewDtoSet = imdbFileReaderService.readAllCrewListFromFile();
        long end = System.currentTimeMillis();
        Assertions.assertThat(crewDtoSet).isNotEmpty();
        log.info("readCrewListFromFileTest Done in " + (end -start) + " millisecond");
    }

    @Test
    void readAliveNameBasicListFromFileTest() throws IOException {
        long start = System.currentTimeMillis();
        Set<NameBasicDto> crewDtoSet = imdbFileReaderService.readNameBasicListFromFile(false);
        long end = System.currentTimeMillis();
        Assertions.assertThat(crewDtoSet).isNotEmpty();
        log.info("readAliveNameBasicListFromFileTest Done in " + (end -start) + " millisecond");

    }

    @Test
    void readBasicListFromFileTest() throws IOException {
        long start = System.currentTimeMillis();
        Set<BasicDto> crewDtoSet = imdbFileReaderService.readBasicListFromFile();
        long end = System.currentTimeMillis();
        Assertions.assertThat(crewDtoSet).isNotEmpty();
        log.info("readBasicListFromFileTest Done in " + (end -start) + " millisecond");
        log.info("Done");
    }

    @Test
    void  readActorInfoTest() throws IOException {
        long start = System.currentTimeMillis();
        NameBasicDto nameBasicDto = imdbFileReaderService.readActorInfo("nm0000009");
        long end = System.currentTimeMillis();
        Assertions.assertThat(nameBasicDto).isNotNull();
        Assertions.assertThat(nameBasicDto.getPrimaryName()).isEqualTo("Richard Burton");
        Assertions.assertThat(nameBasicDto.getDeathYear()).isEqualTo("1984");
        log.info("readActorInfoTest Done in " + (end -start) + " millisecond");
    }
}
