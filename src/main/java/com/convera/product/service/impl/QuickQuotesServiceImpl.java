package com.convera.product.service.impl;

import com.convera.product.constants.ErrorConstants;
import com.convera.product.data.api.QuickQuotesApi;
import com.convera.product.data.model.QuoteRequest;
import com.convera.product.data.model.QuoteResponse;
import com.convera.product.data.model.QuotesResponse;
import com.convera.product.exception.DataNotFoundException;
import com.convera.product.service.QuickQuotesService;
import datadog.trace.api.Trace;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

@Service("QuickQuotesService")
@Slf4j
public class QuickQuotesServiceImpl implements QuickQuotesService {

    @Autowired
    QuickQuotesApi quickQuotesApi;

    @Override
    @Trace
<<<<<<< HEAD
 //   @Cacheable(cacheNames = "quickQuoteCacheByCustomerId", key = "#customerId")
=======
    @Cacheable(cacheNames = "quickQuotesCache", key = "'#customerId'")
>>>>>>> 3ce2f843ef84df5ca9476f2325af492689de7228
    public QuotesResponse getQuickQuotesByCustomerId(String customerId, String correlationId) {
        QuotesResponse response;
        try {
            response = quickQuotesApi.getQuickQuotes(customerId, correlationId);
        } catch (Exception e) {
            throw new DataNotFoundException(String.format(ErrorConstants.QUOTES_DATA_ERROR, customerId));
        }
        if (!ObjectUtils.isEmpty(response) && CollectionUtils.isEmpty(response.getData())) {
            log.error(ErrorConstants.QUOTES_NOT_FOUND, customerId);
            throw new DataNotFoundException(String.format(ErrorConstants.QUOTES_NOT_FOUND, customerId));
        }
        return response;
    }

    @Override
    @Trace
<<<<<<< HEAD
 //   @Cacheable(cacheNames = "quickQuoteCacheByQuoteId", key = "#quoteId")
=======
    //@Cacheable(cacheNames = "quickQuotesById", key = "'#quoteId'")
>>>>>>> 3ce2f843ef84df5ca9476f2325af492689de7228
    public QuoteResponse getQuickQuoteByQuoteId(String quoteId, String correlationId) {
        QuoteResponse response;
        try {
            response = quickQuotesApi.getQuickQuote(quoteId, correlationId);
        } catch (Exception e) {
            throw new DataNotFoundException(String.format(ErrorConstants.QUOTE_DATA_ERROR, quoteId));
        }

        if (ObjectUtils.isEmpty(response) || ObjectUtils.isEmpty(response.getData())) {
            log.error(ErrorConstants.QUOTE_NOT_FOUND, quoteId);
            throw new DataNotFoundException(String.format(ErrorConstants.QUOTE_NOT_FOUND, quoteId));
        }
        return response;
    }

    @Override
    @Trace
    public QuoteResponse saveQuickQuote(QuoteRequest request, String correlationId) {
        QuoteResponse response;
        try {
            response = quickQuotesApi.saveQuickQuote(request,correlationId);
        } catch (HttpClientErrorException.BadRequest ex) {
            log.error("dashboard-service bad request getLocalizedMessage:", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("dashboard-service", ex);
            throw ex;
        }
        return response;
    }
}
