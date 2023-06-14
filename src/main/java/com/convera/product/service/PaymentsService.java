package com.convera.product.service;


import com.convera.payments.data.model.PaymentDetailsAndPayeeesResponseBean;
import com.convera.payments.data.model.PaymentDetailsRequest;

public interface PaymentsService {
    PaymentDetailsAndPayeeesResponseBean getPaymentDetails (PaymentDetailsRequest paymentDetailsRequest, int startIndex, int pageSize, String correlationId);
}
