package com.spedire.Spedire.services.location.google;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.spedire.Spedire.services.location.google.LocationUtils.*;


@Service
@Slf4j
public class LocationApis implements LocationService {

    @Value("AIzaSyAFJ857mY26RWOoPQzpcMc1pJRjVEC5QoI")
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
    public JSONObject getCoordinatesWithAddress(String address) throws Exception {
        String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";
        String query = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = baseUrl + "?address=" + query + "&key=" + GOOGLE_APIKEY;

        HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());
        if ("OK".equals(jsonResponse.getString("status"))) {
            JSONArray results = jsonResponse.getJSONArray("results");
            if (!results.isEmpty()) {
                JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                JSONObject coordinates = new JSONObject();
                coordinates.put("latitude", location.getDouble("lat"));
                coordinates.put("longitude", location.getDouble("lng"));
                return coordinates;
            } else {
                throw new Exception("No results found for the given address.");
            }
        } else {
            throw new Exception("Geocoding API error: " + jsonResponse.getString("status"));
        }
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
