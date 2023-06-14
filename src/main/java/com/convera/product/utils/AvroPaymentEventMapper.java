package com.convera.product.utils;

import com.convera.product.model.PaymentEvent;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class AvroPaymentEventMapper {


    public static PaymentEvent toPaymentEvent(com.convera.avro.schemas.PaymentEvent avroPaymentEvent) {

        if (avroPaymentEvent == null) {
            return null;
        }

        return PaymentEvent.builder()
                .eventId(getValueString(avroPaymentEvent.getEventId()))
                .eventType(getValueString(avroPaymentEvent.getEventType()))
                .transactionAccountId(getValueString(avroPaymentEvent.getTransactionAccountId()))
                .parentAccountId(getValueString(avroPaymentEvent.getParentAccountId()))
                .occurredOn(avroPaymentEvent.getOccurredOn())
                .externalPaymentId(getValueString(avroPaymentEvent.getExternalPaymentId()))
                .paymentTransactionStatus(getValueString(avroPaymentEvent.getPaymentTransactionStatus()))
                .paymentTransactionStatusReason(getValueString(avroPaymentEvent.getPaymentTransactionStatusReason()))
                .receiveAmount(avroPaymentEvent.getReceiveAmount())
                .receiveCurrencyCode(getValueString(avroPaymentEvent.getReceiveCurrencyCode()))
                .payeeId(getValueString(avroPaymentEvent.getPayeeId()))
                .payeeName(getValueString(avroPaymentEvent.getPayeeName()))
                .paymentConfirmationNumber(getValueString(avroPaymentEvent.getPaymentConfirmationNumber()))
                .build();
    }

    private static String getValueString(CharSequence val) {
        if(val==null){
            return null;
        }
        return val.toString();
    }

    public static List<PaymentEvent> toPaymentEventLit(Set<com.convera.avro.schemas.PaymentEvent> setAvroPaymentEvent) {
        if (CollectionUtils.isEmpty(setAvroPaymentEvent)) {
            return Collections.EMPTY_LIST;
        }
        return setAvroPaymentEvent.stream().map(AvroPaymentEventMapper::toPaymentEvent).collect(Collectors.toList());
    }

}
