package com.pse.fotoz.remote.ws;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 * Currency conversion endpoint using the fixer.io API (http://api.fixer.io/).
 * @author Robert
 */
public class FixerIo {
    
    private static final String endpoint = "http://api.fixer.io/latest";
    
    private static JSONObject latestRates;
    
    /**
     * Gives the current exchange rates for currencies in JSON format.
     * Uses the fixer.io API (https://api.fixer.io/).
     * The exchange rates are cached for one day.
     * @return Current exchange rates for currencies.
     * @throws com.pse.fotoz.remote.ws.FixerIo.ServiceUnavailableException if
     * there was an error obtaining or parsing the data from the remote source.
     */
    public static JSONObject getRates() throws ServiceUnavailableException {
        if (latestRates == null) {
            refresh();
        } else {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").
                        parse(latestRates.getString("date"));
                if ( (new Date().getTime() - date.getTime() ) / 
                        (24 * 60 * 60 * 1000) > 1) {
                    refresh();
                }
            } catch (ParseException ex) {
                throw new ServiceUnavailableException("Service is "
                        + "unavailable.");
            }
        }
        
        return latestRates.getJSONObject("rates");
    }

    /**
     * Refreshes the exchange rate by making a new call to the web service.
     * @throws com.pse.fotoz.remote.ws.FixerIo.ServiceUnavailableException if
     * there was an error obtaining or parsing the data from the remote source.
     */
    private static void refresh() throws ServiceUnavailableException {
        try {
            latestRates = new JSONObject(IOUtils.toString(new URL(endpoint)));
        } catch (IOException ex) {
            throw new ServiceUnavailableException("Service is "
                    + "unavailable.");
        }
    }

    /**
     * Exception describing the unavailability of this service.
     */
    public static class ServiceUnavailableException extends Exception {

        public ServiceUnavailableException(String message) {
            super(message);
        }
    }
    
}
