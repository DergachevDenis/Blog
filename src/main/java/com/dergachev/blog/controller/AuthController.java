package com.dergachev.blog.controller;

import com.dergachev.blog.dto.ForgotPasswordRequest;
import com.dergachev.blog.dto.ResetPasswordRequest;
import com.dergachev.blog.exception.UserException;
import com.dergachev.blog.dto.AuthRequest;
import com.dergachev.blog.dto.RegistrationRequest;
import com.dergachev.blog.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class AuthController {

    private final UserServiceImpl userService;

    @Autowired
    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.register(registrationRequest);
        } catch (UserException userException) {
            response.put("error", userException);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception exception) {
            log.error("IN registerUser - {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", String.format("The user has been created. Follow the link that we sent to %s to activate your account", registrationRequest.getEmail()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> authResponse(@Valid @RequestBody AuthRequest request, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            response.put("Token:", userService.auth(request));
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("IN authResponse - {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/confirm/{code}")
    public ResponseEntity<Map<String, Object>> activate(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();

        try {
            userService.activateUser(code);
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("IN activate - {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/auth/forgot_password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.forgotPasswordEmail(forgotPasswordRequest);
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity(response, HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("IN forgotPassword - {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/auth/reset")
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest request, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.resetPassword(request);
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("IN resetPassword - {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean getMapResponseError(BindingResult bindingResult, Map<String, Object> response) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                response.put(error.getField(), error.getDefaultMessage());
            }
            return true;
        }
        return false;
    }
}
