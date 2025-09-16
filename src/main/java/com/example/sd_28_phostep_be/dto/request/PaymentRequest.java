package com.example.sd_28_phostep_be.dto.request;

import java.math.BigDecimal;

public class PaymentRequest {
    private Integer invoiceId;
    private BigDecimal amount;
    private BigDecimal vnpayAmount;
    private BigDecimal cashAmount;
    private String paymentMethod; // "VnPay", "Tiền mặt", "Cả 2"

    public PaymentRequest() {}

    public PaymentRequest(Integer invoiceId, BigDecimal amount, String paymentMethod) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getVnpayAmount() {
        return vnpayAmount;
    }

    public void setVnpayAmount(BigDecimal vnpayAmount) {
        this.vnpayAmount = vnpayAmount;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
