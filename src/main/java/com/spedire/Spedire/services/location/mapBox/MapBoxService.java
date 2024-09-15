package com.spedire.Spedire.services.location.mapBox;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

public interface MapBoxService {

    JSONObject getCoordinatesFromAddressV6(String address) throws URISyntaxException, InterruptedException, IOException;

    public JSONObject getCoordinatesFromAddressV5(String address) throws URISyntaxException, Exception;
    public String getMinutesAway(String senderLocation, String destination) throws Exception;


}
