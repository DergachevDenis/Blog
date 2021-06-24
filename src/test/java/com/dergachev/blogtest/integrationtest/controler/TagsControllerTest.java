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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        BlogDispatcherInit.class,
        BlogWebInit.class,
        BlogWebConfig.class,
        SecurityConfig.class,
        RedisConfig.class,
        JPAConfig.class
         })
public class TagsControllerTest {

    @Autowired
    WebApplicationContext wac;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getTagsCloudTest() throws Exception {
        mvc.perform(get("/admin")).andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
}
