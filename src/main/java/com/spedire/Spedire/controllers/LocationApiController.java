package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.services.location.LocationApis;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/location")
@RestController
public class LocationApiController {

    private final LocationApis locationApis;


    @PostMapping("/nearbyPlaces")
    public ResponseEntity<?> getNearbyPlaces () {
        List<String> response;
        try {
            response = locationApis.getNearbyPlaces();
            if (!response.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().success(true).message("Closest Landmarks").data(response).build());
            else return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().success(false).message("No Landmarks").build());
        } catch (URISyntaxException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }

    }



}
