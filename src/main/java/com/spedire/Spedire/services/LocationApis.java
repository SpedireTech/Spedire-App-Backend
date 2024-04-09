package com.spedire.Spedire.services;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class LocationApis {

    public static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpClient httpClient = HttpClient.newHttpClient();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public JSONObject getLocationCoordinates() throws URISyntaxException, IOException, InterruptedException {
        log.info("do this legwork");
        String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyAGHpgeiFAzUQqrosmbd2G531zmD9zgiI8";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.noBody()
                ).
            uri(new URI(baseUrl)).build();
        var res = sendRequest(request);
        log.info(res.body() + " this is res");
        JSONObject object = new JSONObject(res.body());
        log.info(object.getJSONObject("location") + "this is location");
        return object.getJSONObject("location");
    }









}
