package com.spedire.Spedire.services.location;
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

import static com.spedire.Spedire.services.location.LocationUtils.*;


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

//    public static JSONObject getCoordinatesFromAddress(String address) throws URISyntaxException, IOException, InterruptedException, IOException {
//        String baseUrl = "https://api.mapbox.com/search/geocode/v6/forward";
//        String encodedAddress = address.replace(" ", "%20").replace(",", "%2C");
//
//        String requestUrl = String.format("%s?address=%s&access_token=%s", baseUrl, encodedAddress, MAPBOX_APIKEY);
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder().uri(new URI(requestUrl)).GET().build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        JSONObject responseObject = new JSONObject(response.body());
//        if (responseObject.has("features")) {
//            JSONObject firstFeature = responseObject.getJSONArray("features").getJSONObject(0);
//            JSONObject geometry = firstFeature.getJSONObject("geometry");
//            JSONArray coordinatesArray = geometry.getJSONArray("coordinates");
//            double longitude = coordinatesArray.getDouble(0);
//            double latitude = coordinatesArray.getDouble(1);
//
//            JSONObject coordinates = new JSONObject();
//            coordinates.put("longitude", longitude);
//            coordinates.put("latitude", latitude);
//
//            return coordinates;
//        } else {
//            throw new RuntimeException("No features found in the response.");
//        }
//    }


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
                .uri(new URI(url))
                .header("Content-Type", Content_Type)
                .header("X-Goog-Api-Key", GOOGLE_APIKEY)
                .header("X-Goog-FieldMask", X_Goog_FieldMask)
                .build();
        var response = sendRequest(request);
        return getLandmarks(response);
    }














}
