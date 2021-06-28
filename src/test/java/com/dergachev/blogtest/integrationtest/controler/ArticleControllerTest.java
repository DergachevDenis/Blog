package com.dergachev.blogtest.integrationtest.controler;

import com.dergachev.blog.config.BlogDispatcherInit;
import com.dergachev.blog.config.BlogWebInit;
import com.dergachev.blog.config.BlogWebConfig;
import com.dergachev.blog.config.SecurityConfig;
import com.dergachev.blog.config.RedisConfig;
import com.dergachev.blog.config.JPAConfig;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.article.ArticleStatus;
import com.dergachev.blog.entity.article.Tag;
import com.dergachev.blog.entity.user.RoleEntity;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.entity.user.UserStatus;
import com.dergachev.blog.exception.ExceptionResponse;
import com.dergachev.blog.jwt.JwtProvider;
import com.dergachev.blog.repository.ArticleRepository;
import com.dergachev.blog.repository.RoleEntityRepository;
import com.dergachev.blog.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@EnableWebSecurity
@TestPropertySource("classpath:/blogtest.properties")
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
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RoleEntityRepository roleEntityRepository;
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    private final Gson gsonInstance = new Gson();

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
    }

    @Test
    public void addArticleWithoutAuthorization() throws Exception {
        this.mvc.perform(post("/articles"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(value = "/dropTables.sql")
    public void addArticleWithInvalidBody() throws Exception {
        User user = createUser();
        String token = getTokenFromUser(user);

        MvcResult mvcResult = this.mvc.perform(post("/articles").header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ExceptionResponse exceptionResponse = gsonInstance.fromJson(body, ExceptionResponse.class);

        assertEquals("Invalid body", exceptionResponse.getMessage());
    }

    @Test
    @Sql(value = "/dropTables.sql")
    public void getArticles() throws Exception {
        User user = createUser();
        String token = getTokenFromUser(user);

        Article article = new Article();
        fillArticle(user, article);
        articleRepository.save(article);

        MvcResult mvcResult = this.mvc.perform(get("/articles").header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        Type collectionType = new TypeToken<List<Article>>() {}.getType();
        List<Article> articleList = gsonInstance.fromJson(body, collectionType);

        assertEquals(1, articleList.size());
        assertEquals(article, articleList.get(0));
    }

    @Test
    @Sql(value = "/dropTables.sql")
    public void addArticle() throws Exception {
        User user = createUser();
        String token = getTokenFromUser(user);
        ArticleRequest articleRequest = new ArticleRequest();
        fillArticleRequest(articleRequest);

        String json = gsonInstance.toJson(articleRequest);

        this.mvc.perform(post("/articles").header("Authorization", "Bearer " + token).contentType("application/json").content(json))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    private String getTokenFromUser(User user) {
        return jwtProvider.generateToken(user.getEmail());
    }

    private User createUser() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setStatus(UserStatus.ACTIVE);
        user.setLastName("testLast");
        user.setFirstName("testFirst");
        RoleEntity authorRole = roleEntityRepository.findByName("ROLE_USER");
        user.setRoleEntity(authorRole);
        userRepository.save(user);
        return user;
    }

    private void fillArticle(User user, Article article) {
        article.setUser(user);
        article.setTitle("Test");
        article.setText("Test text");
        article.setStatus(ArticleStatus.PUBLIC);
        article.setCreatedAt("12.12.2021");
        article.setUpdateAt("12.12.2021");
    }

    private void fillArticleRequest(ArticleRequest articleRequest) {
        articleRequest.setTitle("Test");
        articleRequest.setText("Test text");
        articleRequest.setStatus("Public");
        articleRequest.setTags(new ArrayList<Tag>());
    }
}
