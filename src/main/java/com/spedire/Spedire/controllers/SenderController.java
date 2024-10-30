package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.SelectCarrierRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.carrier.CarrierService;
import com.spedire.Spedire.services.sender.SenderService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Sender", description = "Endpoints related to Sender")
public class SenderController {

    private final SenderService senderService;




}
