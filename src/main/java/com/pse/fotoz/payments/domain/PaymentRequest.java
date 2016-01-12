package com.pse.fotoz.payments.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Optional;
import org.springframework.util.LinkedMultiValueMap;

/**
 * Payment object for creating a new payment, see https://www.mollie.com/en/docs/reference/payments/create
 * @author René
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    private Double amount;
    private String description;
    private String redirectUrl;
    private String webhookUrl; //optional
    private String method;//optional
    // ->deze vereist JSOn parsen + lelijke exception handling, is nog niet nodig voor ons project dus even geskipt
    //private Map<String,String> metadata;//optional
    private Locale locale;//optional

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setAmount(int amount) {
        this.amount = Double.valueOf(amount);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Optional<String> getWebhookUrl() {
        return Optional.ofNullable(this.webhookUrl);
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public Optional<String> getMethod() {
        return Optional.ofNullable(this.method);
    }

    public void setMethod(String method) {
        this.method = method;
    }

//    public Optional<Map<String,String>> getMetadata() {
//        return Optional.ofNullable(this.metadata);
//    }
//
//    public void setMetadata(Map<String,String> metadata) {
//        this.metadata = metadata;
//    }

    public Optional<Locale> getLocale() {
        return Optional.ofNullable(this.locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    /***
     * Used for creating a LinkedMultiValueMap, which is used by Resttemplate
     * @return 
     */
    public LinkedMultiValueMap<String, String> toMap(){
        //deze methode(objectmapper) is het mooiste, werkt perfect met een Map, alleen Jacksons Objectmapper en springs multivaluemap
        //bijten elkaar op een vage wijze...geen elegantere oplossing nog kunnen vinden(mvm is nodig voor resttamplate), evt reflection?
//        ObjectMapper m = new ObjectMapper();
//        Map<String, List<String>> props = m.convertValue(this, Map.class);
//        LinkedMultiValueMap<String, String> mm = new LinkedMultiValueMap<>(props);

        //ObjectMapper m = new ObjectMapper();
        LinkedMultiValueMap<String, String> returnMap = new LinkedMultiValueMap<>();
        returnMap.add("amount", this.getAmount().toString());
        returnMap.add("description", this.getDescription());
        returnMap.add("redirectUrl", this.getRedirectUrl());
        if(this.getWebhookUrl().isPresent()) {returnMap.add("webhookUrl", this.getWebhookUrl().toString()); }
        if(this.getMethod().isPresent()) {returnMap.add("method", this.getMethod().toString()); }
        //if(this.getMetadata().isPresent()) {returnMap.add("metadata", m.writeValueAsString(this.getMetadata())); }
        if(this.getLocale().isPresent()) {returnMap.add("locale", this.getLocale().get().getValue()); }
        return returnMap;
    }
    
    /**
     * Locales supported by Mollie
     *
     * @author René
     */
    public enum Locale {

        GERMANY("de"),
        ENGLAND("en"),
        SPAIN("es"),
        FRANCE("fr"),
        BELGIUM("be"),
        BELGIUM_FRENCH("be-fr"),
        NETHERLANDS("nl");

        private String value;

        private Locale(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return this.value;
        }
    }
}
