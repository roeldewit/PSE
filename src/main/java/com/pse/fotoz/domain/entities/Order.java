package com.pse.fotoz.domain.entities;

import com.pse.fotoz.payments.domain.PaymentResponse.PaymentMethod;
import com.pse.fotoz.payments.domain.PaymentResponse.PaymentStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Entity describing an order of one or more items (OrderEntries). An order is
 * associated to a customer account.
 *
 * @author Robert
 */
@Entity
@Table(name = "orders")
public class Order implements HibernateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount account;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    @Cascade({CascadeType.SAVE_UPDATE})
    private Set<OrderEntry> entries;

    @Column(name = "mollie_payment_id")
    private String molliePaymentID;

    @Column(name = "mollie_payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus molliePaymentStatus;

    @Column(name = "mollie_payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod molliePaymentMethod;

    @Column(name = "mollie_payment_created_date")
    private Date molliePaymentCreatedDate;

    @Column(name = "mollie_payment_paid_date")
    private Date molliePaymentPaidDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CustomerAccount getAccount() {
        return account;
    }

    public void setAccount(CustomerAccount account) {
        this.account = account;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Set<OrderEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<OrderEntry> entries) {
        this.entries = entries;
    }

    public String getMolliePaymentID() {
        return molliePaymentID;
    }

    public void setMolliePaymentID(String molliePaymentID) {
        this.molliePaymentID = molliePaymentID;
    }

    public PaymentStatus getMolliePaymentStatus() {
        return molliePaymentStatus;
    }

    public void setMolliePaymentStatus(PaymentStatus molliePaymentStatus) {
        this.molliePaymentStatus = molliePaymentStatus;
    }

    public PaymentMethod getMolliePaymentMethod() {
        return molliePaymentMethod;
    }

    public void setMolliePaymentMethod(PaymentMethod molliePaymentMethod) {
        this.molliePaymentMethod = molliePaymentMethod;
    }

    public Date getMolliePaymentCreatedDate() {
        return molliePaymentCreatedDate;
    }

    public void setMolliePaymentCreatedDate(Date molliePaymentCreatedDate) {
        this.molliePaymentCreatedDate = molliePaymentCreatedDate;
    }

    public Date getMolliePaymentPaidDateTime() {
        return molliePaymentPaidDateTime;
    }

    public void setMolliePaymentPaidDateTime(Date molliePaymentPaidDateTime) {
        this.molliePaymentPaidDateTime = molliePaymentPaidDateTime;
    }

    public static enum OrderStatus {
        PLACED, PAID, PROCESSED
    }

    /**
     * Provides the sum of all prices of entries in this order. This is the
     * amount billed were the user placing this order.
     *
     * @return Total price of all items in this order.
     */
    public double getPriceSum() {
        return this.getEntries().stream().
                map(e -> e.getTotalPrice()).
                reduce(0d, (d1, d2) -> d1 + d2);
    }
}
