package com.spedire.Spedire.services.location;

import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LocationUtils {

    public static String X_Goog_FieldMask = "places.displayName";
    public static String Content_Type = "application/json";


    @SneakyThrows
    public static HttpResponse<String> sendRequest(HttpRequest request) {
        HttpResponse<String> response;
        HttpClient httpClient = HttpClient.newHttpClient();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public static List<String> getLandmarks(HttpResponse<String> response)  {
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray placesArray = jsonResponse.getJSONArray("places");
        List<String> landmarks = new ArrayList<>();

        for (int i = 0; i < placesArray.length(); i++) {
            JSONObject place = placesArray.getJSONObject(i);
            JSONObject displayName = place.getJSONObject("displayName");
            String text = displayName.getString("text");
            landmarks.add(text);
        }

        return landmarks;
    }

    public static JSONObject getRequestBody(double longitude, double lat) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("maxResultCount", 10);
        JSONObject locationRestriction = new JSONObject();
        JSONObject circle = new JSONObject();
        JSONObject center = new JSONObject();
        center.put("latitude", lat);
        center.put("longitude", longitude);
        circle.put("center", center);
        circle.put("radius", 1500.0);
        locationRestriction.put("circle", circle);
        requestBody.put("locationRestriction", locationRestriction);
        return requestBody;
    }


}
