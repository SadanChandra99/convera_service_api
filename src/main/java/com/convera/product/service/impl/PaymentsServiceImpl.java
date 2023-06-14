package com.convera.product.service.impl;

import com.convera.payments.data.api.PaymentApi;
import com.convera.payments.data.model.PaymentDetailsAndPayeeesResponseBean;
import com.convera.payments.data.model.PaymentDetailsRequest;
import com.convera.product.constants.ErrorConstants;
import com.convera.product.exception.ServiceExternalException;
import com.convera.product.service.PaymentsService;
import datadog.trace.api.Trace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


@Service("PaymentsService")
@Slf4j
public class PaymentsServiceImpl implements PaymentsService {

    @Autowired
    PaymentApi getPaymentApi;

    @Trace
<<<<<<< HEAD
 //   @Cacheable(cacheNames = "paymentCache", key = "#account")
    public PaymentsResponse getPayment(String account, String correlationId) {
        PaymentsResponse payments;
        try {
            
            payments = paymentsApi.getPayments(account,correlationId);
           
        } catch (Exception e) {
            throw new DataNotFoundException(String.format(ErrorConstants.PAYMENT_DATA_ERROR, account));
=======
    @Override
    @Cacheable(cacheNames = "paymentCache", key = "'#paymentDetailsRequest.transactionAccountId'")
    public PaymentDetailsAndPayeeesResponseBean getPaymentDetails(PaymentDetailsRequest paymentDetailsRequest, int startIndex, int pageSize, String correlationId) {
        PaymentDetailsAndPayeeesResponseBean payments;
        try {
            log.info("invoking PaymentApi");
            payments = getPaymentApi.getPaymentDetailsByTransactionAccountId(paymentDetailsRequest,startIndex,pageSize, correlationId);
            return payments;
        } catch (HttpClientErrorException.BadRequest ex) {
            log.error("PaymentsService Bad Request", ex);
            throw ex;
        } catch (HttpClientErrorException.NotFound ex) {
            log.error("PaymentsService Not Found", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("PaymentsService something went wrong", ex);
            throw new ServiceExternalException(String.format(ErrorConstants.PAYMENT_DATA_ERROR, ex.getMessage()));
>>>>>>> 3ce2f843ef84df5ca9476f2325af492689de7228
        }
    }
}
