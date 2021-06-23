package com.dergachev.blog.controler;

import com.dergachev.blog.config.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        BlogWebConfig.class,
        SecurityConfig.class,
        RedisConfig.class,
      JPAConfig.class
         }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
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

   /* @GetMapping
    public ResponseEntity<Map<String, Integer>> getTagsCloud() {
        Map<String, Integer> cloudTags = tagService.getTagsCloud();
        return new ResponseEntity<>(cloudTags, HttpStatus.OK);
    }*/

}
