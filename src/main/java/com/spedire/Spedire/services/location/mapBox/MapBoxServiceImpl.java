package com.spedire.Spedire.services.location.mapBox;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

@Service
public class MapBoxServiceImpl implements MapBoxService {

    private static final String MAPBOX_API_URL = "https://api.mapbox.com/directions-matrix/v1/mapbox";

    @Value("${mapbox.apikey}")
    private String MAPBOX_APIKEY;
    private final OkHttpClient client = new OkHttpClient();


    @Override
    public JSONObject getCoordinatesFromAddressV6(String address) throws URISyntaxException, InterruptedException, IOException {
        String[] addressParts = address.split(",\\s*");
        String houseNumberAndStreet = addressParts[0];
        String[] streetParts = houseNumberAndStreet.split(" ", 2);
        String houseNumber = streetParts[0];
        String street = streetParts[1];
        String place = addressParts[1] + ", " + addressParts[2];
        String baseUrl = "https://api.mapbox.com/search/geocode/v6/forward";

        String encodedHouseNumber = houseNumber.replace(" ", "%20").replace(",", "%2C");
        String encodedStreet = street.replace(" ", "%20").replace(",", "%2C");
        String encodedPlace = place.replace(" ", "%20").replace(",", "%2C");

        String requestUrl = String.format("%s?country=ng&address_number=%s&street=%s&place=%s&access_token=%s",
                baseUrl, encodedHouseNumber, encodedStreet, encodedPlace, MAPBOX_APIKEY);
        return null;
    }


    public String getMinutesAway(String senderLocation, String carrierLocation) throws Exception {
        JSONObject originCoordinates = getCoordinatesFromAddressV5(senderLocation);
        JSONObject destinationCoordinates = getCoordinatesFromAddressV5(carrierLocation);

        double originLongitude = originCoordinates.getDouble("longitude");
        double originLatitude = originCoordinates.getDouble("latitude");
        double destinationLongitude = destinationCoordinates.getDouble("longitude");
        double destinationLatitude = destinationCoordinates.getDouble("latitude");

        String url = String.format("%s/walking/%f,%f;%f,%f?access_token=%s",
                MAPBOX_API_URL, originLongitude, originLatitude, destinationLongitude, destinationLatitude, MAPBOX_APIKEY);

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            JSONObject responseJson = new JSONObject(responseBody);

            if (responseJson.has("durations")) {
                JSONArray durationsArray = responseJson.getJSONArray("durations");
                double durationInSeconds = durationsArray.getJSONArray(0).getDouble(1);
                double durationInMinutes = durationInSeconds / 60;
                int minutesAway = (int) Math.round(durationInMinutes);
                return minutesAway + " min away";
            } else {
                throw new RuntimeException("No durations found in the response.");
            }
        }
    }



    @Override
    public JSONObject getCoordinatesFromAddressV5(String address) throws URISyntaxException, Exception {
        String baseUrl = "https://api.mapbox.com/geocoding/v5/mapbox.places/";
        String query = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = baseUrl + query + ".json?access_token=" + MAPBOX_APIKEY;
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray centerCoordinates = jsonResponse.getJSONArray("features").getJSONObject(0).getJSONArray("center");
        JSONObject coordinates = new JSONObject();
        coordinates.put("longitude", centerCoordinates.getDouble(0));
        coordinates.put("latitude", centerCoordinates.getDouble(1));
        return coordinates;
    }

}
