package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.CompleteRegistrationResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RequestMapping("/api/v1/user/")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("savePhoneNumber/{phoneNumber}")
    public ResponseEntity<ApiResponse<?>> savePhoneNumber(@PathVariable String phoneNumber)  {
        VerifyPhoneNumberResponse response = new VerifyPhoneNumberResponse();
        try {
           response = userService.savePhoneNumber(phoneNumber);
           return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(true).build());
       } catch (IllegalArgumentException exception) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(response.getMessage()).success(false).build());
       }
    }


    @PostMapping("complete-registration")
    public ResponseEntity<ApiResponse<?>> completeRegistration(@RequestBody CompleteRegistrationRequest request, @RequestHeader("Authorization") String token)  {
        CompleteRegistrationResponse response = new CompleteRegistrationResponse();
        try {
            response = userService.completeRegistration(request, token);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(true).build());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(response.getMessage()).success(false).build());
        }

    }






}
