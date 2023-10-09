package com.lobox.imdblobox;

import com.lobox.imdblobox.controller.dto.CrewDto;
import com.lobox.imdblobox.controller.dto.NameBasicDto;
import com.lobox.imdblobox.service.ImdbFileReaderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class IMDBLoboxApplicationTests {

	@Autowired
	private  ImdbFileReaderServiceImpl imdbFileReaderService;


	@Test
	void readNameBasicListFromFileTest() throws IOException {
		Set<NameBasicDto> nameBasicDtoList= imdbFileReaderService.readNameBasicListFromFile();
		Assertions.assertThat(nameBasicDtoList).isNotEmpty();
		log.info("Done");
	}

	@Test
	void readCrewListFromFileTest()  throws IOException{
		Set<CrewDto> crewDtoList = imdbFileReaderService.readAllCrewListFromFile();
		Assertions.assertThat(crewDtoList).isNotEmpty();
		log.info("Done");
	}

}
