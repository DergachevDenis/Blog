package com.dergachev.blogtest.junittest.config;

import com.dergachev.blog.jwt.JwtProvider;
import com.dergachev.blog.repository.ArticleRepository;
import com.dergachev.blog.repository.CommentRepository;
import com.dergachev.blog.repository.RoleEntityRepository;
import com.dergachev.blog.repository.UserRepository;
import com.dergachev.blog.service.MailSenderService;
import com.dergachev.blog.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

@Configuration()
@PropertySource("classpath:/blog.properties")
public class TestConfig {

    @Bean
    public RoleEntityRepository getRoleEntityRepository() {
        return mock(RoleEntityRepository.class);
    }

    @Bean
    public UserRepository getUserRepository() {
        return mock(UserRepository.class);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return mock(PasswordEncoder.class);
    }

    @Bean
    public RedisTemplate<String, String> getTemplate() {
        return mock(RedisTemplate.class);
    }

    @Bean
    public MailSenderService getMailSenderService() {
        return mock(MailSenderService.class);
    }

    @Bean
    public JwtProvider getJwtProvider() {
        return mock(JwtProvider.class);
    }

    @Bean
    public CommentRepository getCommentRepository() {
        return mock(CommentRepository.class);
    }

    @Bean
    public ArticleRepository getArticleRepository() {
        return mock(ArticleRepository.class);
    }

    @Bean
    public UserServiceImpl getUserService() {
        return new UserServiceImpl(getRoleEntityRepository(), getUserRepository(),
                getPasswordEncoder(), getTemplate(),
                getMailSenderService(), getJwtProvider());
    }

}
