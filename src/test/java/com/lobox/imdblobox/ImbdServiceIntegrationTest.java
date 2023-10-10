package com.lobox.imdblobox;

import com.lobox.imdblobox.service.ImdbFileReaderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class ImbdServiceIntegrationTest {

    @Autowired
    private ImdbFileReaderServiceImpl imdbFileReaderService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void readTitlesWithSameDirectorAndWriterTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v0.1/imdb/read-same-director-and-writer")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        //Do other check here with mvcResult
    }



    @Test
    void readTitlesThatActorsPlayedAtTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v0.1/imdb/read-based-on-actors")
                        .param("firstActorId","0001")
                        .param("secondActorId","0002")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        //Do other check here with mvcResult
    }

    @Test
    void readTitlesBasedOnGenreAndVoteTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v0.1/imdb/read-based-on-genre")
                        .param("genre","Comedy")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        //Do other check here with mvcResult
    }

    @Test
    void  readTotalRequestCountTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v0.1/imdb/read-total-request-count")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        //Do other check here with mvcResult
    }

}
