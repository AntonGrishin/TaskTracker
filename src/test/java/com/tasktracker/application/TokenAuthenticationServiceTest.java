package com.tasktracker.application;

import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.Assert.assertNotNull;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;

import org.junit.jupiter.api.Test;


@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TokenAuthenticationServiceTest {


    // private String adminTokenValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYwODQ2NzAxNCwiZXhwIjoxNjA4NTUzNDE0fQ.7B1dLJC8-dS-_sNJ2lr3sFuTzRnJxwldLSzl5-hX87b_-Frg6achNUaVwRn6rmdCS9CzuYEI9LqsE1m_N_3fAw";
    // private String userTokenValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvZG1lbiIsImlhdCI6MTYwODQ2NzA5OCwiZXhwIjoxNjA4NTUzNDk4fQ.ST9IqgZv3nSxsuZM0IXonIBnxkIudAf3_T1V9us32nRozt9bMqHOVCO6A7dW7FpNU2B8dXt4uxeQ6oXLhN5_hA";


    private String usernameAdmin= "admin";
    private String passwordAdmin = "password";

    private String usernameUser = "odmen";
    private String passwordUser = "12345678";


    @Value("${server.port}")
    private int portNumber;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldNotAllowAccessToUnauthorizedUsers() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/users")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldGenerateAuthToken() throws Exception {
       String token = TokenAuthenticationService.createToken("admin");


        assertNotNull(token);
        mvc.perform(MockMvcRequestBuilders.get("/api/users").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }


    @Test
    public void existentUserCanGetTokenAndAuthentication() throws Exception {
    // String username = "admin";
    // String password = "password";

    String body = "{\"username\":" + "\"" + usernameAdmin + "\"," + " \"password\":" + "\""+  passwordAdmin + "\"" + "}";

    MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
            .contentType("application/json").content(body))
            .andExpect(status().isOk()).andReturn();

    JSONObject object = new JSONObject(result.getResponse().getContentAsString());
    String token = object.getString("accessToken");
    System.out.println(token);

    mvc.perform(MockMvcRequestBuilders.get("/api/users")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
    }

    @Test
    public void shouldNotAllowAccessToUnauthUsers() throws Exception {
        // String username = "odmen";
        // String password = "12345678";
    
        String body = "{\"username\":" + "\"" + usernameUser + "\"," + " \"password\":" + "\""+  passwordUser + "\"" + "}";
    
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .contentType("application/json").content(body))
                .andExpect(status().isOk()).andReturn();
    
        JSONObject object = new JSONObject(result.getResponse().getContentAsString());
        String token = object.getString("accessToken");
        System.out.println(token);
    
        mvc.perform(MockMvcRequestBuilders.get("/api/users/2")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
        }

    @Test
    public void newUserCannotBeUpdatedByUser() throws Exception {
        String newusername = "odmenka";
        String newpassword = "12345678";
        String firstname = "First";
        String lastname = "Last";
        String email = "aaabbb@ddd.ccc";
        String role = "user";
        String baseSalary = "20000";
    
        String newUser = "{\"username\":" + "\"" + newusername + "\"," + 
                       "\"firstname\":" + "\"" + firstname + "\"," + 
                      " \"lastname\":" + "\""+  lastname + "\"," + 
                      " \"email\":" + "\""+  email + "\"," + 
                      " \"password\":" + "\""+  newpassword + "\"," + 
                      " \"role\":" + "[\""+  role + "\"] ," + 
                      " \"baseSalary\":" + "\"" + baseSalary + "\"}";

        String body = "{\"username\":" + "\"" + usernameUser + "\"," + " \"password\":" + "\""+  passwordUser + "\"" + "}";
    
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .contentType("application/json").content(body))
                .andExpect(status().isOk()).andReturn();
    
        JSONObject object = new JSONObject(result.getResponse().getContentAsString());
        String token = object.getString("accessToken");
    
        mvc.perform(MockMvcRequestBuilders.post("/api/user_update").contentType("application/json").content(newUser).header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());

        }

    //@Test
    public void newUserCanBeUpdatedByAdmin() throws Exception {
        String username = "odmenka";
        String password = "12345678";
        String firstname = "First";
        String lastname = "Last";
        String email = "aaabbb@ddd.ccc";
        String role = "user";
        String baseSalary = "20000";
    
        String newUser = "{\"username\":" + "\"" + username + "\"," + 
                        "\"firstname\":" + "\"" + firstname + "\"," + 
                        " \"lastname\":" + "\""+  lastname + "\"," + 
                        " \"email\":" + "\""+  email + "\"," + 
                        " \"password\":" + "\""+  password + "\"," + 
                        " \"role\":" + "[\""+  role + "\"]," +
                        " \"baseSalary\":" + "\"" + baseSalary + "\"}";

        String body = "{\"username\":" + "\"" + usernameAdmin + "\"," + " \"password\":" + "\""+  passwordAdmin + "\"" + "}";

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .contentType("application/json").content(body))
                .andExpect(status().isOk()).andReturn();
    
        JSONObject object = new JSONObject(result.getResponse().getContentAsString());
        String token = object.getString("accessToken");
        System.out.println(token);
    
        mvc.perform(MockMvcRequestBuilders.post("/api/user_update").contentType("application/json").content(newUser).header("Authorization", "Bearer " + token)).andExpect(status().isOk());

        }
    

  }

