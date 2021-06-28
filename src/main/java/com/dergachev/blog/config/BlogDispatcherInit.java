package com.dergachev.blog.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class BlogDispatcherInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                BlogWebConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{
                SecurityConfig.class,
                RedisConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{
                "/*"
        };
    }
}
