package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.SelectCarrierRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.carrier.CarrierService;
import com.spedire.Spedire.services.sender.SenderService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/api/v1/sender")
@RestController
@Slf4j
public class SenderController {

    private final SenderService senderService;
    private final CarrierService carrierService;

    @GetMapping("/find-match")
    public ResponseEntity<ApiResponse<?>> findMatch(@RequestParam String orderId) {
        try {
            var matchResults = senderService.findMatch(orderId, carrierService);
            return ResponseEntity.ok(
                    ApiResponse.builder().message("Matches found").success(true).data(matchResults).build());
        } catch (SpedireException e) {
            log.error("SpedireException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder().message("Order not found: " + e.getMessage()).success(false).build());
        } catch (Exception e) {
            log.error("General error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder().message("An error occurred: " + e.getMessage()).success(false).build());
        }

    }


    @PostMapping("/select-carrier")
    public ResponseEntity<ApiResponse<?>> selectCarrier(@RequestBody SelectCarrierRequest request) {
        try {
            Object response = senderService.selectCarrier(request);

            return ResponseEntity.ok(
                    ApiResponse.builder().message("Carrier selection successful").success(true).data(response).build());
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder().message("Failed to send notification email: " + e.getMessage()).success(false).build());
        } catch (SpedireException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder().message(e.getMessage()).success(false).build());
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder().message("An unexpected error occurred: " + e.getMessage()).success(false).build());
        }
    }


}
