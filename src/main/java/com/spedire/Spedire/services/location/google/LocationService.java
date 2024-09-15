package com.spedire.Spedire.services.location.google;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public interface LocationService {


    JSONObject getLocationCoordinates() throws URISyntaxException;

    List<String> getNearbyPlaces() throws URISyntaxException;


}
