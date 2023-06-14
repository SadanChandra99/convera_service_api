package com.convera.product.service.impl;

import com.convera.product.constants.ErrorConstants;
import com.convera.product.data.api.QuickQuotesApi;
import com.convera.product.data.model.*;
import com.convera.product.exception.DataNotFoundException;
import com.convera.product.exception.ServiceExternalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class QuickQuotesServiceImplTest {

    @InjectMocks
    QuickQuotesServiceImpl quickQuotesService;

    @Mock
    QuickQuotesApi quickQuotesApi;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getQuickQuotesByCustomerId() {

        String correlationId = "ea9231bf-e7e4-4cc9-add6-eebc85582ac9";
        String customerId = "1068914";
        String quoteId = "3143ff29-f3be-475e-a0df-e17ba9a73f5d";

        QuotesResponse quotesResponse = new QuotesResponse();

        
        Quote quote = new Quote();
        quote.customerId(customerId);
        quote.quoteId(quoteId);
        quote.receiveCurrency("USD");
        quote.payCurrency("CAD");
        quote.specifiedAmount(new BigDecimal(2));
        quote.specifiedCurrency("CAD");
        quote.valueDate("20220825");
        quote.customerRate(new BigDecimal(2));
        quote.customerRateInverted(random(new BigDecimal(2),7));
        quote.baseCurrency("USD");
        quote.costRate(random(new BigDecimal(2),2));
        quote.receiveAmount(random(new BigDecimal(100),1));
        quote.payAmount(random(new BigDecimal(100),1));
        quote.expirationInterval(2);
        quote.forwardPoints(random(new BigDecimal(10),0));
        quote.salesMargin(random(new BigDecimal(1),7));

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        Map<String, Quote> data = new HashMap<>();
        data.put(quoteId,quote);

        quotesResponse.setData(data);
        quotesResponse.setMetadata(metadata);

        Mockito.when(quickQuotesApi.getQuickQuotes(any(),any())).thenReturn(quotesResponse);
        Assertions.assertNotNull(quickQuotesService.getQuickQuotesByCustomerId(customerId, correlationId));
    }


    @Test
    void getQuickQuotesByCustomerIdWithNotFound() {

        String correlationId = "ea9231bf-e7e4-4cc9-add6-eebc85582ac9";
        String customerId = "1068914";
        String quoteId = "3143ff29-f3be-475e-a0df-e17ba9a73f5d";

        QuotesResponse quotesResponse = new QuotesResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        quotesResponse.setMetadata(metadata);

        Mockito.when(quickQuotesApi.getQuickQuotes(any(),any())).thenReturn(quotesResponse);
        Exception thrown = Assertions.assertThrows(DataNotFoundException.class, () -> {
            quickQuotesService.getQuickQuotesByCustomerId(customerId, correlationId);
        });

        Assertions.assertEquals(String.format(ErrorConstants.QUOTES_NOT_FOUND, customerId), thrown .getMessage());
    }

    @Test
    void getQuickQuotesByCustomerIdWithServiceError() {

        String correlationId = "ea9231bf-e7e4-4cc9-add6-eebc85582ac9";
        String customerId = "1068914";
        String quoteId = "3143ff29-f3be-475e-a0df-e17ba9a73f5d";

        QuotesResponse quotesResponse = new QuotesResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        quotesResponse.setMetadata(metadata);

        Mockito.when(quickQuotesApi.getQuickQuotes(any(),any())).thenThrow(new RestClientException(""));

        Exception thrown = Assertions.assertThrows(DataNotFoundException.class, () -> {
            quickQuotesService.getQuickQuotesByCustomerId(customerId, correlationId);
        });

        Assertions.assertEquals(String.format(ErrorConstants.QUOTES_DATA_ERROR, customerId), thrown .getMessage());
    }


    @Test
    void getQuickQuoteByQuoteId() {
        String correlationId = "ea9231bf-e7e4-4cc9-add6-eebc85582ac9";
      String customerId = "1068914";
        String quoteId = "3143ff29-f3be-475e-a0df-e17ba9a73f5d";

        QuoteResponse quoteResponse = new QuoteResponse();
        Quote quote = new Quote();
        quote.customerId(customerId);
        quote.quoteId(quoteId);
        quote.receiveCurrency("USD");
        quote.payCurrency("CAD");
        quote.specifiedAmount(new BigDecimal(2));
        quote.specifiedCurrency("CAD");
        quote.valueDate("20220825");
        quote.customerRate(new BigDecimal(2));
        quote.customerRateInverted(random(new BigDecimal(2),7));
        quote.baseCurrency("USD");
        quote.costRate(random(new BigDecimal(2),2));
        quote.receiveAmount(random(new BigDecimal(100),1));
        quote.payAmount(random(new BigDecimal(100),1));
        quote.expirationInterval(2);
        quote.forwardPoints(random(new BigDecimal(10),0));
        quote.salesMargin(random(new BigDecimal(1),7));

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        quoteResponse.setMetadata(metadata);
        quoteResponse.setData(quote);

        Mockito.when(quickQuotesApi.getQuickQuote(any(),any())).thenReturn(quoteResponse);

        QuoteResponse result = quickQuotesService.getQuickQuoteByQuoteId(quoteId, correlationId);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals(quoteId, result.getData().getQuoteId());
        Assertions.assertNotNull(result.getMetadata());
        Assertions.assertEquals(200, result.getMetadata().getStatusCode());
    }

    @Test
    void getQuickQuoteByQuoteIdWithServiceError() {
        String correlationId = "ea9231bf-e7e4-4cc9-add6-eebc85582ac9";
//      String customerId = "1068914";
        String quoteId = "3143ff29-f3be-475e-a0df-e17ba9a73f5d";

        QuotesResponse quotesResponse = new QuotesResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        quotesResponse.setMetadata(metadata);

        Mockito.when(quickQuotesApi.getQuickQuote(any(),any())).thenThrow(new RestClientException(""));

        Exception thrown = Assertions.assertThrows(DataNotFoundException.class, () -> {
            quickQuotesService.getQuickQuoteByQuoteId(quoteId, correlationId);
        });

        Assertions.assertEquals(String.format(ErrorConstants.QUOTE_DATA_ERROR, quoteId), thrown .getMessage());
    }

    @Test
    void getQuickQuoteByQuoteIdWithNotFound() {
        String correlationId = "ea9231bf-e7e4-4cc9-add6-eebc85582ac9";
//      String customerId = "1068914";
        String quoteId = "3143ff29-f3be-475e-a0df-e17ba9a73f5d";

        QuoteResponse quoteResponse = new QuoteResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        quoteResponse.setMetadata(metadata);

        Mockito.when(quickQuotesApi.getQuickQuote(any(),any())).thenReturn(quoteResponse);

        Exception thrown = Assertions.assertThrows(DataNotFoundException.class, () -> {
            quickQuotesService.getQuickQuoteByQuoteId(quoteId, correlationId);
        });

        Assertions.assertEquals(String.format(ErrorConstants.QUOTE_NOT_FOUND, quoteId), thrown .getMessage());
    }

    @Test
    void saveQuickQuote() {
        String correlationId = "ea9231bf-e7e4-4cc9-add6-eebc85582ac9";
        String customerId = "1068914";

        QuoteResponse quoteResponse = new QuoteResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        quoteResponse.setMetadata(metadata);
        quoteResponse.setData(new Quote());

        QuoteRequest request = new QuoteRequest();
        request.setCustomerId(customerId);
        request.customerId(customerId);
        request.receiveCurrency("USD");
        request.payCurrency("CAD");
        request.specifiedCurrency("USD");
        request.specifiedAmount(new BigDecimal(100));
        request.payReceive("PAY");

        Mockito.when(quickQuotesApi.saveQuickQuote(any(),any())).thenReturn(quoteResponse);

        QuoteResponse result = quickQuotesService.saveQuickQuote(request, correlationId);
        Assertions.assertNotNull(result);
    }

    @Test
    void saveQuickQuoteWithNotFound() {
        String correlationId = "ea9231bf-e7e4-4cc9-add6-eebc85582ac9";
        String quoteId = "3143ff29-f3be-475e-a0df-e17ba9a73f5d";

        QuoteResponse quoteResponse = new QuoteResponse();

        ResponseMetadata metadata = new ResponseMetadata();
        metadata.errors(null);
        metadata.setStatusCode(200);

        quoteResponse.setMetadata(metadata);

        QuoteRequest request = new QuoteRequest();

        Mockito.when(quickQuotesApi.saveQuickQuote(any(),any())).thenThrow(new RestClientException(""));

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            quickQuotesService.saveQuickQuote(request, correlationId);
        });

        Assertions.assertNotNull(thrown);
    }

    BigDecimal random(BigDecimal range, int scale) {
        return new BigDecimal(Math.random()).multiply(range).setScale(scale, BigDecimal.ROUND_DOWN);
    }
}