package com.dergachev.blog.controller;

import com.dergachev.blog.dto.ForgotPasswordRequest;
import com.dergachev.blog.dto.ResetPasswordRequest;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.exception.UserException;
import com.dergachev.blog.dto.AuthRequest;
import com.dergachev.blog.dto.RegistrationRequest;
import com.dergachev.blog.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest,
                                                            BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            User user = new User();
            userService.register(registrationRequest);
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.put("message",
                String.format("The user has been created. Follow the link that we sent to %s to activate your account", registrationRequest.getEmail()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/auth", produces = "application/json")
    public ResponseEntity<Map<String, String>> authResponse(@Valid @RequestBody AuthRequest request,
                                                            BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            response.put("Token:", userService.auth(request));
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/auth/confirm/{code}", produces = "application/json")
    public ResponseEntity<Map<String, String>> activate(@PathVariable String code) {
        Map<String, String> response = new HashMap<>();

        try {
            userService.activateUser(code);
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/auth/forgot_password", produces = "application/json")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest,
                                                              BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.forgotPasswordEmail(forgotPasswordRequest);
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity(response, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/auth/reset", produces = "application/json")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                             BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.resetPassword(request);
        } catch (UserException userException) {
            response.put("error", userException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean getMapResponseError(BindingResult bindingResult, Map<String, String> response) {
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
