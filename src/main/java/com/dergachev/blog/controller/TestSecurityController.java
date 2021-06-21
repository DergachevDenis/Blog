package com.dergachev.blog.controller;


import com.dergachev.blog.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestSecurityController {

    @Autowired
    private MailSender mailSender;

    @GetMapping("/exception")
    public void getException(){
        throw new NotFoundException("Hello world!");
    }

    @GetMapping("/admin")
    public String getAdmin() {
        System.out.println("Hello");
        return "Hi admin";
    }

    @GetMapping("/sendEmail")
    public String sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("testemailblog93@gmail.com");
        message.setTo("dergache.di@gmail.com");
        message.setSubject("Notification");
        message.setText("Denis is petyx https://github.com/");
        this.mailSender.send(message);

        return "Email Sent!";
    }

    @GetMapping("/user")
    public String getUser() {
        return "Hi user";
    }

}
