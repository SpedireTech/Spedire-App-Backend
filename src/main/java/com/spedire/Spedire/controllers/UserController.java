package com.spedire.Spedire.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/api/v1/user/")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/verifyPhoneNumberFirst/{phoneNumber}")
    public ResponseEntity<ApiResponse<?>> verifyPhoneNumberFirst(@PathVariable("phoneNumber") String phoneNumber) {
        try {
            VerifyPhoneNumberResponse response = userService.verifyUserPhoneNumberFirst(phoneNumber);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                    .success(true).message(response.getMessage()).data(response).build());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.builder().message(exception.getMessage()).build());
        }

    }
}
