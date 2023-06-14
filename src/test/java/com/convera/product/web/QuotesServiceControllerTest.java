package com.convera.product.web;


import com.convera.product.QuickQuotesHelper;

import com.convera.product.data.model.*;
import com.convera.product.service.QuickQuotesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QuotesServiceController.class)

public class QuotesServiceControllerTest {


    private static final String BASE_PATH = "/convera/quick-quotes";

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    QuickQuotesService quickQuotesService;

    @Test
    void getQuickQuotes_success() throws Exception {
        String customerId = "12345";
        Map<String, Quote> mapResult = QuickQuotesHelper.getQuickQuotes(customerId,3);
        when(quickQuotesService.getQuickQuotesByCustomerId(any(),any()))
                .thenReturn(new QuotesResponse(
                        mapResult,
                        new ResponseMetadata(LocalDateTime.now(), 200, "success", BASE_PATH + "/",
                                Collections.emptyList(), "412fba3a-1517-4dfb-9eba-3195b21b9bc8")));

        MvcResult mvcResult = this.mockMvc.perform(get(BASE_PATH + "/").param("customerId","12345"))
                .andExpect(status().isOk()).andReturn();
        int statusCode = mvcResult.getResponse().getStatus();

        assertEquals(200, statusCode);
        assertNotNull(mvcResult.getResponse());
    }

    @Test
    void shouldThrowNotFoundWhenNotExist() throws Exception {
        doThrow(HttpClientErrorException.NotFound.class).when(quickQuotesService).getQuickQuotesByCustomerId(any(),any());
        this.mockMvc.perform(get(BASE_PATH + "/").param("customerId","12345")).andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowExceptionWhenErrorOccurred() throws Exception {
        doThrow(HttpServerErrorException.InternalServerError.class).when(quickQuotesService).getQuickQuotesByCustomerId(any(),any());
        this.mockMvc.perform(get(BASE_PATH + "/").param("customerId","12345")).andExpect(status().isInternalServerError());
    }

    @Test
    void getQuickQuote_success() throws Exception {
        String quoteId = "412fba3a-1517-4dfb-9eba-3195b21b9bc8";
        Quote quote = QuickQuotesHelper.getQuickQuoteByQuoteId(quoteId);

        when(quickQuotesService.getQuickQuoteByQuoteId(any(),any()))
                .thenReturn(new QuoteResponse(
                        quote,
                        new ResponseMetadata(LocalDateTime.now(), 200, "success", BASE_PATH + "/",
                                Collections.emptyList(), "412fba3a-1517-4dfb-9eba-3195b21b9bc8")));

        MvcResult mvcResult = this.mockMvc.perform(get(BASE_PATH + "/"+quoteId))
                .andExpect(status().isOk()).andReturn();
        int statusCode = mvcResult.getResponse().getStatus();

        assertEquals(200, statusCode);
        assertNotNull(mvcResult.getResponse());
    }

    @Test
    void getQuickQuote_shouldThrowNotFoundWhenNotExist() throws Exception {
        String quoteId = "412fba3a-1517-4dfb-9eba-3195b21b9bc8";
        doThrow(HttpClientErrorException.NotFound.class).when(quickQuotesService).getQuickQuoteByQuoteId(any(),any());
        this.mockMvc.perform(get(BASE_PATH + "/"+quoteId)).andExpect(status().isNotFound());
    }

    @Test
    void getQuickQuote_shouldThrowExceptionWhenErrorOccurred() throws Exception {
        String quoteId = "412fba3a-1517-4dfb-9eba-3195b21b9bc8";
        doThrow(HttpServerErrorException.InternalServerError.class).when(quickQuotesService).getQuickQuoteByQuoteId(any(),any());
        this.mockMvc.perform(get(BASE_PATH + "/"+quoteId)).andExpect(status().isInternalServerError());
    }

    @Test
    void saveQuickQuote_success() throws Exception {
        String quoteId = "412fba3a-1517-4dfb-9eba-3195b21b9bc8";
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setPayCurrency("USD");
        quoteRequest.setReceiveCurrency("CAD");
        quoteRequest.setSpecifiedAmount(new BigDecimal(10));
        quoteRequest.setSpecifiedCurrency("CAD");
        quoteRequest.payReceive("PAY");

        Quote quote = QuickQuotesHelper.getQuickQuoteByQuoteId(quoteId);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(quoteRequest);

        when(quickQuotesService.saveQuickQuote(any(),any()))
                .thenReturn(new QuoteResponse(
                        quote,
                        new ResponseMetadata(LocalDateTime.now(), 200, "success", BASE_PATH ,
                                Collections.emptyList(), "412fba3a-1517-4dfb-9eba-3195b21b9bc8")));

        MvcResult mvcResult = this.mockMvc.perform(
                        post(BASE_PATH + "/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk()).andReturn();
        int statusCode = mvcResult.getResponse().getStatus();

        assertEquals(200, statusCode);
        assertNotNull(mvcResult.getResponse());
    }

    @Test
    void saveQuickQuote_shouldThrowExceptionWhenErrorOccurred() throws Exception {
        String quoteId = "412fba3a-1517-4dfb-9eba-3195b21b9bc8";
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setPayCurrency("USD");
        quoteRequest.setReceiveCurrency("CAD");
        quoteRequest.setSpecifiedAmount(new BigDecimal(10));
        quoteRequest.setSpecifiedCurrency("CAD");
        quoteRequest.payReceive("PAY");

        Quote quote = QuickQuotesHelper.getQuickQuoteByQuoteId(quoteId);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(quoteRequest);

        doThrow(HttpServerErrorException.InternalServerError.class).when(quickQuotesService).saveQuickQuote(any(),any());
        this.mockMvc.perform(
                post(BASE_PATH + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    void saveQuickQuote_shouldThrowBadRequest() throws Exception {
        String quoteId = "412fba3a-1517-4dfb-9eba-3195b21b9bc8";
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setPayCurrency("USD");
        quoteRequest.setReceiveCurrency("CAD");
        quoteRequest.setSpecifiedAmount(new BigDecimal(10));
        quoteRequest.setSpecifiedCurrency("CAD");
        quoteRequest.payReceive("PAY");

        Quote quote = QuickQuotesHelper.getQuickQuoteByQuoteId(quoteId);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(quoteRequest);
        when(quickQuotesService.saveQuickQuote(any(),any()))
        .thenThrow(HttpClientErrorException.BadRequest.create(HttpStatus.BAD_REQUEST,"400",null,null,null));
        this.mockMvc.perform(
                post(BASE_PATH + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isBadRequest());
    }




}
