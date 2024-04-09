package com.spedire.Spedire.controllers;

import com.spedire.Spedire.services.LocationApis;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/location")
@RestController
public class LocationApiController {

    private final LocationApis locationApis;

    @PostMapping("/getC")
    public String getC() throws URISyntaxException, IOException, InterruptedException {
        return locationApis.getNearbyPlaces();
    }


}
