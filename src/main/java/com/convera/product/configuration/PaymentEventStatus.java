package com.convera.product.configuration;

import java.util.Random;

public enum PaymentEventStatus {

    CREATED,
    FUNDED,
    RELEASED,
    RECEIVED,
    REJECTED,
    CANCELLED,
    REFUNDED;

    private static final Random PRNG = new Random();


    public static PaymentEventStatus random()  {
        PaymentEventStatus[] values = values();
        return values[PRNG.nextInt(values.length)];
    }
}
