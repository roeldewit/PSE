package com.pse.fotoz.payments.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Mappes the links JSON object of the payment response from Mollie
 * Contains links used for the payment
 * @author René
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentLinks {
    private String paymentUrl;
    private String webhookUrl;
    private String redirectUrl;

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
