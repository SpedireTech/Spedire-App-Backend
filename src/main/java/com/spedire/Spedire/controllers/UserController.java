package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.*;
import com.spedire.Spedire.dtos.responses.*;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.spedire.Spedire.controllers.Utils.*;
import static org.apache.http.HttpHeaders.AUTHORIZATION;


@AllArgsConstructor
@RequestMapping("/api/v1/user/")
@RestController
@Slf4j
public class UserController {

    private final UserService userService;


    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegistrationRequest registrationRequest) {
        RegistrationResponse response;
        try {
            response = userService.createUser(registrationRequest);
            if ( response.getOtp() == null) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiResponse.builder().message(INCOMPLETE_REGISTRATION).data(response).success(false).build());
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(SUCCESSFUL).data(response).success(true).build());
            }
        } catch (SpedireException | RedisConnectionFailureException exception) {
            if (exception.getMessage().equals("Unable to connect to Redis")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }

    }

    @PostMapping("verifyPhoneNumber")
    public ResponseEntity<ApiResponse<?>> verifyPhoneNumber(HttpServletRequest httpServletRequest, @RequestBody VerifyPhoneNumberRequest request)  {
        VerifyPhoneNumberResponse response;
        try {
            response = userService.verifyPhoneNumber(httpServletRequest, request.isRoute(), request.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(PROCEED_TO_ENTER_GENERATED_OTP).data(response).success(true).build());
           } catch (SpedireException | RedisConnectionFailureException exception) {
            if (exception.getMessage().equals("Unable to connect to Redis")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


    @GetMapping("dashboard")
    public ResponseEntity<ApiResponse<?>> fetchUserProfile(@RequestHeader(AUTHORIZATION) String token)  {
        ResponseEntity<ApiResponse<?>> authorizationResponse = validateAuthorization(token);
        if (authorizationResponse != null) {
            return authorizationResponse;
        }

        UserDashboardResponse response;
        try {
            response = userService.fetchDashboardInfoForUser(token);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(USER_DASHBOARD_INFO).data(response).success(true).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


    @PutMapping("deliveryStatus/{status}")
    public ResponseEntity<ApiResponse<?>> deliveryStatus(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable boolean status) {

        ResponseEntity<ApiResponse<?>> authorizationResponse = validateAuthorization(token);
        if (authorizationResponse != null) {
            return authorizationResponse;
        }
        try {
            userService.deliveryStatus(status, token);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message("Delivery Status Updated").success(true).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }




    @PostMapping("forgotPassword")
    public ResponseEntity<?> forgotPassword (@RequestBody ForgotPasswordRequest request)  {
        ForgotPasswordResponse response;
        try {
            response = userService.forgotPassword(request);
            if (response.getMessage().equals(String.format(RESET_INSTRUCTIONS_SENT, request.getEmail()))) {
                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(response.isStatus()).build());
            } else if (response.getMessage().equals(MAIL_DELIVERY_FAILED)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder().message(response.getMessage()).success(response.isStatus()).build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(response.getMessage()).success(response.isStatus()).build());
            }
        } catch (SpedireException exception) {
            if (exception.getMessage().equals(FAIL_TO_SEND_MAIL)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }

    @PostMapping("resetPassword")
    public ResponseEntity<?> resetPassword (@RequestBody ChangePasswordRequest request) {
        ChangePasswordResponse response;
        try {
            response = userService.resetPassword(request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(response.isStatus()).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }

    }

    @PostMapping("testing")

    public String testing(){
        return "Spedire server is working";
    }






}
