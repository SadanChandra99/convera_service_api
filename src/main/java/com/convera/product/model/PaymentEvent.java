package com.convera.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent {

    private String eventId;
    private String eventType;
    private String transactionAccountId;
    private String parentAccountId;
    private Instant occurredOn;
    private String externalPaymentId;
    private String paymentTransactionStatus;
    private String paymentTransactionStatusReason;
    private BigDecimal receiveAmount;
    private String receiveCurrencyCode;
    private String payeeId;
    private String payeeName;
    private String paymentConfirmationNumber;
}
