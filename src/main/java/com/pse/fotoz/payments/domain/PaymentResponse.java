/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.payments.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pse.fotoz.payments.domain.enums.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

/**
 * Payment object for getting a  payment, see https://www.mollie.com/en/docs/reference/payments/get
 * @author René
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    private String id;
    private PaymentMode mode;
    private Date createdDatetime;
    private PaymentStatus status;
    private Date paidDateTime;
    private Date cancelledDatetime;
    private Date expiredDatetime;
    private Duration expiryPeriod;
    private BigDecimal amount;
    private String description;
    private PaymentMethod method;
    private Map<String, String> metadata;
    private String locale;
    private String profileId;
    private String settlementId;
    private PaymentLinks links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentMode getMode() {
        return mode;
    }

    public void setMode(PaymentMode mode) {
        this.mode = mode;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Date getPaidDateTime() {
        return paidDateTime;
    }

    public void setPaidDateTime(Date paidDateTime) {
        this.paidDateTime = paidDateTime;
    }

    public Date getCancelledDatetime() {
        return cancelledDatetime;
    }

    public void setCancelledDatetime(Date cancelledDatetime) {
        this.cancelledDatetime = cancelledDatetime;
    }

    public Date getExpiredDatetime() {
        return expiredDatetime;
    }

    public void setExpiredDatetime(Date expiredDatetime) {
        this.expiredDatetime = expiredDatetime;
    }

    public Duration getExpiryPeriod() {
        return expiryPeriod;
    }

    public void setExpiryPeriod(String expiryPeriod) throws DatatypeConfigurationException {
        this.expiryPeriod = DatatypeFactory.newInstance().newDuration(expiryPeriod);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }

    public PaymentLinks getLinks() {
        return links;
    }

    public void setLinks(PaymentLinks links) {
        this.links = links;
    }
    
}
