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

    public static JSONObject getLocationCoordinates() throws URISyntaxException, IOException, InterruptedException {
        String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyAGHpgeiFAzUQqrosmbd2G531zmD9zgiI8";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.noBody()
                ).
            uri(new URI(baseUrl)).build();
        var res = sendRequest(request);
        JSONObject object = new JSONObject(res.body());
        return object.getJSONObject("location");
    }

    public  String getNearbyPlaces() throws URISyntaxException, IOException, InterruptedException {

        JSONObject object = getLocationCoordinates();
        log.info(object + " this is object");
        double longitude = Double.parseDouble(String.valueOf(object.getDouble("lng")));
        double lat = Double.parseDouble(String.valueOf(object.getDouble("lat")));
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+longitude+"&key=AIzaSyAGHpgeiFAzUQqrosmbd2G531zmD9zgiI8";
        HttpRequest request = HttpRequest.newBuilder().GET().uri(new URI(url)).build();
        var res = sendRequest(request);
        return res.body();

    }











}
