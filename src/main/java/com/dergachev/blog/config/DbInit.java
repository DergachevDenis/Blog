package com.dergachev.blog.config;

import com.dergachev.blog.entity.user.RoleEntity;
import com.dergachev.blog.repository.RoleEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DbInit {
    private static final String ROLE_USER = "ROLE_USER";
    @Autowired
    private RoleEntityRepository roleEntityRepository;

    @PostConstruct
    private void postConstruct() {
        RoleEntity roleEntity = roleEntityRepository.findByName(ROLE_USER);
        if (roleEntity == null) {
            roleEntity = new RoleEntity();
            roleEntity.setName(ROLE_USER);
            roleEntityRepository.save(roleEntity);
        }
    }
}
