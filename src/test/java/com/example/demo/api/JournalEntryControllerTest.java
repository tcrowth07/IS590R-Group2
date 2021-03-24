package com.example.demo.api;

import com.example.demo.model.JournalEntry;
import com.example.demo.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WithUserDetails("test")
class JournalEntryControllerTest {

    private WebApplicationContext context;
    private MockMvc mvc;

    @Autowired
    public JournalEntryControllerTest(WebApplicationContext context) {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(context)
                //.apply(springSecurity())
                .build();
    }

    @Test
    void addJournalEntry() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .post("/api/v1/journalentry")
                .content(asJsonString(new JournalEntry(null, "test", "test markdown", "<p>test</p>", UUID.fromString("43ae77fc-d738-47c1-9abf-57c488fc277d"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void getAllJournalEntries() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .get("/api/v1/journalentry")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

//    @Test
//    void getAllJournalEntriesByUserId() throws Exception {
//        mvc.perform( MockMvcRequestBuilders
//                .get("/api/v1/journalentry/user/{id}", "f5b3fe20-d9f9-4f18-95fa-1534aaf584c8")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().is(200));
//    }

    @Test
    void getJournalEntryById() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .get("/api/v1/journalentry/{id}", "fd8c9e74-8bc4-4d09-9981-2c5e501158c8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void deleteJournalEntryById() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .delete("/api/v1/journalentry/{id}", "a2fdc1fb-fbe6-4bc4-864c-be43025e234e")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}