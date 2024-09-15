package com.spedire.Spedire.services.location.google;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.spedire.Spedire.services.location.google.LocationUtils.*;


@Service
@Slf4j
public class LocationApis implements LocationService {

    @Value("${google.location.apikey}")
    private String GOOGLE_APIKEY;

    @Override
    public JSONObject getLocationCoordinates() throws URISyntaxException {
        String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key="+GOOGLE_APIKEY;
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.noBody()).uri(new URI(baseUrl)).build();
        var res = sendRequest(request);
        JSONObject object = new JSONObject(res.body());
        return object.getJSONObject("location");
    }


    @Override
    public List<String> getNearbyPlaces() throws URISyntaxException {
        JSONObject object = getLocationCoordinates();
        log.info(object + " this is object");

        double longitude = Double.parseDouble(String.valueOf(object.getDouble("lng")));
        double latitude = Double.parseDouble(String.valueOf(object.getDouble("lat")));
        String url = "https://places.googleapis.com/v1/places:searchNearby";
        JSONObject requestBody = getRequestBody(longitude, latitude);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .uri(new URI(url)).header("Content-Type", Content_Type)
                .header("X-Goog-Api-Key", GOOGLE_APIKEY).header("X-Goog-FieldMask", X_Goog_FieldMask).build();
        var response = sendRequest(request);
        return getLandmarks(response);
    }














}
