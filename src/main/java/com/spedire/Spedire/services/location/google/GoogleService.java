package com.spedire.Spedire.services.location.google;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

public interface GoogleService {


    JSONObject getLocationCoordinates() throws URISyntaxException;

    public JSONObject getCoordinatesWithAddress(String address) throws Exception;

    List<String> getNearbyPlaces() throws URISyntaxException;


}
