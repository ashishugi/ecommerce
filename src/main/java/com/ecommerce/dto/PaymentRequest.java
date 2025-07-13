
package com.ecommerce.dto;

import com.ecommerce.entity.Payment;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull
    private Long orderId;

    @NotNull
    private Payment.PaymentMethod method;

    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Payment.PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(Payment.PaymentMethod method) {
        this.method = method;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
