package com.convera.product.service.impl;

import com.convera.payments.data.api.PaymentApi;
import com.convera.payments.data.model.PaymentDetailsAndPayeeesResponseBean;
import com.convera.payments.data.model.PaymentDetailsRequest;
import com.convera.product.ReadJsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PaymentsServiceImplTest {


    @InjectMocks
    private PaymentsServiceImpl paymentsService;
    @Mock
    PaymentApi getPaymentApi;

    int startIndex = 1;
    int pageSize = 100;

    @Test
    void getPayment() throws IOException {
        String account = "63d372ef-3b68-4977-9602-f7a48c7756ac";
        String correlationId = "7fea251b-9541-42b5-9d8e-62b099815c88";


        PaymentDetailsRequest paymentDetailsRequest = new PaymentDetailsRequest();
        paymentDetailsRequest.setTransactionAccountId("85");

        PaymentDetailsAndPayeeesResponseBean paymentDetailsAndPayeeesResponseBean = ReadJsonUtil.getFileContent("/ResponsePayment_200.json");

        Mockito.when(getPaymentApi.getPaymentDetailsByTransactionAccountId(any(), any(),any(), any())).thenReturn((paymentDetailsAndPayeeesResponseBean));
        PaymentDetailsAndPayeeesResponseBean result = paymentsService.getPaymentDetails(paymentDetailsRequest, startIndex, pageSize, correlationId);

        Assertions.assertNotNull(result);
    }


    @Test
    void getPaymentWithNotFound() throws IOException {
        String correlationId = "7fea251b-9541-42b5-9d8e-62b099815c88";
        PaymentDetailsRequest paymentDetailsRequest = new PaymentDetailsRequest();
        paymentDetailsRequest.setTransactionAccountId("5");

        PaymentDetailsAndPayeeesResponseBean paymentDetailsAndPayeeesResponseBean = ReadJsonUtil.getFileContent("/ResponsePayment_404.json");

        Mockito.when(getPaymentApi.getPaymentDetailsByTransactionAccountId(any(), any(), any(), any())).thenReturn(paymentDetailsAndPayeeesResponseBean);
        PaymentDetailsAndPayeeesResponseBean result = paymentsService.getPaymentDetails(paymentDetailsRequest, startIndex, pageSize, correlationId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), result.getMetadata().getStatusCode());

    }

    @Test
    void getPaymentWithBadRequest() throws IOException {

        String correlationId = "7fea251b-9541-42b5-9d8e-62b099815c88";
        PaymentDetailsRequest paymentDetailsRequest = new PaymentDetailsRequest();
        paymentDetailsRequest.setTransactionAccountId("5");

        PaymentDetailsAndPayeeesResponseBean paymentDetailsAndPayeeesResponseBean = ReadJsonUtil.getFileContent("/ResponsePayment_400.json");



        Mockito.when(getPaymentApi.getPaymentDetailsByTransactionAccountId(any(), any(), any(), any())).thenReturn(paymentDetailsAndPayeeesResponseBean);
        PaymentDetailsAndPayeeesResponseBean result = paymentsService.getPaymentDetails(paymentDetailsRequest, startIndex, pageSize, correlationId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getMetadata().getStatusCode());

    }


    @Test
    void getPaymentWithNotFound_404() throws IOException {
        String correlationId = "7fea251b-9541-42b5-9d8e-62b099815c88";
        PaymentDetailsRequest paymentDetailsRequest = new PaymentDetailsRequest();
        paymentDetailsRequest.setTransactionAccountId("5");

        String r = ReadJsonUtil.getFileContentString("/ResponsePayment_404.json");

        Mockito.when(getPaymentApi.getPaymentDetailsByTransactionAccountId(any(), any(),any(), any())).thenThrow(
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, "", null, r.getBytes(StandardCharsets.UTF_8), null)
        );


        HttpClientErrorException t = Assertions.assertThrows(HttpClientErrorException.class, () -> {
            paymentsService.getPaymentDetails(paymentDetailsRequest, startIndex, pageSize, correlationId);
        });

        Assertions.assertNotNull(t);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(),t.getStatusCode().value());

    }

    @Test
    void getPaymentWithBadRequest_400() throws IOException {

        String correlationId = "7fea251b-9541-42b5-9d8e-62b099815c88";
        PaymentDetailsRequest paymentDetailsRequest = new PaymentDetailsRequest();
        paymentDetailsRequest.setTransactionAccountId("5");
        String r = ReadJsonUtil.getFileContentString("/ResponsePayment_400.json");


        Mockito.when(getPaymentApi.getPaymentDetailsByTransactionAccountId(any(), any(), any(), any())).thenThrow(
                HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "", null, r.getBytes(StandardCharsets.UTF_8), null)
        );

        HttpClientErrorException t = Assertions.assertThrows(HttpClientErrorException.class, () -> {
            paymentsService.getPaymentDetails(paymentDetailsRequest, startIndex, pageSize, correlationId);
        });

        Assertions.assertNotNull(t);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),t.getStatusCode().value());

    }


}