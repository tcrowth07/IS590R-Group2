package com.example.demo.api;

import com.example.demo.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;
//import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WithUserDetails("test")
class UserControllerTest {

    private WebApplicationContext context;
    private MockMvc mvc;

    @Autowired
    public UserControllerTest(WebApplicationContext context) {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(context)
                //.apply(springSecurity())
                .build();
    }

    @Test
    void addUser() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .post("/api/v1/user")
                .content(asJsonString(new User(null, "test", "test@gmail.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    @DisplayName("Should return a list of all users")
    void shouldGetAllUsers() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserById() throws Exception{
        mvc.perform( MockMvcRequestBuilders
                .get("/api/v1/user/{id}", "f5b3fe20-d9f9-4f18-95fa-1534aaf584c8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void deleteUserById() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .delete("/api/v1/user/{id}", "43ae77fc-d738-47c1-9abf-57c488fc277d")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void updateUser() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .put("/api/v1/user/{id}", "43ae77fc-d738-47c1-9abf-57c488fc277d")
                .content(asJsonString(new User(null, "test", "test@gmail.com")))
                .contentType(MediaType.APPLICATION_JSON)
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