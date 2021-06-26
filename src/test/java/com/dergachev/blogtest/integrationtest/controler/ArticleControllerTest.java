package com.dergachev.blogtest.integrationtest.controler;

import com.dergachev.blog.config.BlogDispatcherInit;
import com.dergachev.blog.config.BlogWebInit;
import com.dergachev.blog.config.BlogWebConfig;
import com.dergachev.blog.config.SecurityConfig;
import com.dergachev.blog.config.RedisConfig;
import com.dergachev.blog.config.JPAConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@EnableWebSecurity
@ContextConfiguration(classes = {
        BlogDispatcherInit.class,
        BlogWebInit.class,
        BlogWebConfig.class,
        SecurityConfig.class,
        RedisConfig.class,
        JPAConfig.class
         })
public class ArticleControllerTest {

    @Autowired
    WebApplicationContext wac;

    private MockMvc mvc;

    private static String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZXJnYWNoZS5kaUBnbWFpbC5jb20iLCJleHAiOjE2MjY3Mjg0MDB9.zPMOmjk9eprJMw-BXggDCvbRx_-XRXeu8D0rRow-UECmNfguslM0hZ1-T6LQ_-J4LvFo7M5RLFU4AI2mFwAagQ";

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
    }
    @Test
    public void addArticleWithoutAuthorization() throws Exception {
        this.mvc.perform(post("/articles"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void addArticleWithoutBody() throws Exception {
        this.mvc.perform(post("/articles").header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getArticles() throws Exception {
        this.mvc.perform(get("/articles").header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
